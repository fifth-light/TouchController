package top.fifthlight.touchcontroller.platform

import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import top.fifthlight.touchcontroller.platform.proxy.ProxyPlatform
import top.fifthlight.touchcontroller.platform.win32.Win32Platform
import top.fifthlight.touchcontroller.proxy.server.localhostLauncherSocketProxyServer
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.copyTo
import kotlin.jvm.optionals.getOrNull

object PlatformProvider {
    private val logger = LoggerFactory.getLogger(PlatformProvider::class.java)

    private fun load(): Platform? {
        val systemName = System.getProperty("os.name")
        val systemArch = System.getProperty("os.arch")
        logger.info("System name: $systemName, system arch: $systemArch")

        return if (System.getProperty("os.name").startsWith("Windows")) {
            // Windows
            val targetArch = when (systemArch) {
                "x86_32", "x86", "i386", "i486", "i586", "i686" -> "i686-pc-windows-gnu"
                "amd64", "x86_64" -> "x86_64-pc-windows-gnu"
                else -> null
            } ?: run {
                logger.warn("Unsupported system arch")
                return null
            }
            logger.info("Target arch: $targetArch")

            val loader = FabricLoader.getInstance()

            val nativeLibrary = if (!loader.isDevelopmentEnvironment) {
                val container = loader.getModContainer("top_fifthlight_touchcontroller_proxy-windows").getOrNull() ?: run {
                    logger.warn("Failed to get mod container")
                    return null
                }
                val nativeLibraryPath = "$targetArch/proxy_windows.dll"
                container.findPath(nativeLibraryPath).getOrNull() ?: run {
                    logger.info("Failed to get native library: $nativeLibraryPath")
                    return null
                }
            } else {
                Path.of("..", "..", "target", targetArch, "release", "proxy_windows.dll")
            }

            val destinationFile = try {
                Files.createTempFile("proxy_windows", ".dll").also {
                    logger.info("Extracting native library to $it")
                    nativeLibrary.copyTo(it, true)
                }
            } catch (ex: Exception) {
                logger.warn("Failed to extract native library", ex)
                return null
            }

            logger.info("Loading native library")
            try {
                System.load(destinationFile.toAbsolutePath().toString())
            } catch (ex: Exception) {
                return null
            }
            logger.info("Loaded native library")

            Win32Platform()
        } else {
            // Proxy
            val socketPort = System.getenv("TOUCH_CONTROLLER_PROXY")?.toIntOrNull() ?: return null
            val proxy = localhostLauncherSocketProxyServer(socketPort) ?: return null
            ProxyPlatform(proxy)
        }
    }

    val platform by lazy {
        load()
    }
}