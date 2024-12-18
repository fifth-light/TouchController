package top.fifthlight.touchcontroller.platform

import kotlinx.io.IOException
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import top.fifthlight.touchcontroller.platform.android.AndroidPlatform
import top.fifthlight.touchcontroller.platform.proxy.ProxyPlatform
import top.fifthlight.touchcontroller.platform.win32.Win32Platform
import top.fifthlight.touchcontroller.proxy.server.localhostLauncherSocketProxyServer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.DosFileAttributeView
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFilePermission
import kotlin.io.path.copyTo
import kotlin.io.path.exists
import kotlin.io.path.fileAttributesView
import kotlin.jvm.optionals.getOrNull

object PlatformProvider {
    private val logger = LoggerFactory.getLogger(PlatformProvider::class.java)

    private val isAndroid: Boolean by lazy {
        // Detect the existence of /system/build.prop
        val path = Path.of("/", "system", "build.prop")
        try {
            path.exists()
        } catch (ex: SecurityException) {
            logger.warn("Failed to access $path: ", ex)
            true
        } catch (ex: IOException) {
            logger.warn("Failed to access $path: ", ex)
            true
        }
    }

    private fun getNativeLibraryPath(containerName: String, containerPath: String, debugPath: Path?): Path? {
        val loader = FabricLoader.getInstance()
        return if (!loader.isDevelopmentEnvironment) {
            val container =
                loader.getModContainer(containerName).getOrNull() ?: run {
                    logger.warn("Failed to get mod container")
                    return null
                }
            container.findPath(containerPath).getOrNull() ?: run {
                logger.info("Failed to get library: $containerPath")
                return null
            }
        } else {
            debugPath ?: run {
                logger.warn("No debug library for your platform")
                null
            }
        }
    }

    private fun extractNativeLibrary(prefix: String, suffix: String, path: Path): Path =
        Files.createTempFile(prefix, suffix).also {
            logger.info("Extracting native library to $it")
            path.copyTo(it, true)
        }

    private data class NativeLibraryInfo(
        val targetArch: String,
        val modContainerName: String,
        val modContainerPath: String,
        val debugPath: Path?,
        val extractPrefix: String,
        val extractSuffix: String,
        val readOnlySetter: (Path) -> Unit = {},
        val platformFactory: () -> Platform
    )

    private fun load(): Platform? {
        val socketPort = System.getenv("TOUCH_CONTROLLER_PROXY")?.toIntOrNull()
        if (socketPort != null) {
            val proxy = localhostLauncherSocketProxyServer(socketPort) ?: return null
            return ProxyPlatform(proxy)
        }

        val systemName = System.getProperty("os.name")
        val systemArch = System.getProperty("os.arch")
        logger.info("System name: $systemName, system arch: $systemArch")

        val info = if (systemName.startsWith("Windows")) {
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

            NativeLibraryInfo(
                targetArch = targetArch,
                modContainerName = "top_fifthlight_touchcontroller_proxy-windows",
                modContainerPath = "$targetArch/proxy_windows.dll",
                debugPath = Path.of("..", "..", "target", targetArch, "release", "proxy_windows.dll"),
                extractPrefix = "proxy_windows",
                extractSuffix = ".dll",
                readOnlySetter = {
                    val attributeView = it.fileAttributesView<DosFileAttributeView>()
                    attributeView.setReadOnly(true)
                },
                platformFactory = ::Win32Platform
            )
        } else if (systemName.startsWith("Linux")) {
            if (isAndroid) {
                val targetArch = try {
                    Runtime.getRuntime().exec(arrayOf("getprop", "ro.product.cpu.abi"), null).inputStream.reader()
                        .readText()
                } catch (ex: Exception) {
                    logger.warn("Failed to run getprop", ex)
                    return null
                }

                NativeLibraryInfo(
                    targetArch = targetArch,
                    modContainerName = "top_fifthlight_touchcontroller_proxy-server-android",
                    modContainerPath = "$targetArch/libproxy_server_android.so",
                    debugPath = null,
                    extractPrefix = "libproxy_server_android",
                    extractSuffix = ".so",
                    readOnlySetter = {
                        val attributeView = it.fileAttributesView<PosixFileAttributeView>()
                        // 500
                        attributeView.setPermissions(
                            setOf(
                                PosixFilePermission.OWNER_WRITE,
                                PosixFilePermission.OWNER_EXECUTE
                            )
                        )
                    },
                    platformFactory = ::AndroidPlatform
                )
                return null
            } else {
                logger.warn("Linux is not supported for now!")
                return null
            }
        } else {
            logger.warn("Unsupported system: $systemName")
            return null
        }

        val nativeLibrary = getNativeLibraryPath(
            containerName = info.modContainerName,
            containerPath = info.modContainerPath,
            debugPath = info.debugPath
        ) ?: return null

        val destinationFile = try {
            extractNativeLibrary(info.extractPrefix, info.extractSuffix, nativeLibrary)
        } catch (ex: Exception) {
            logger.warn("Failed to extract native library", ex)
            return null
        }

        try {
            info.readOnlySetter.invoke(destinationFile)
        } catch (ex: Exception) {
            logger.info("Failed to set file $destinationFile read-only", ex)
        }

        logger.info("Loading native library")
        try {
            System.load(destinationFile.toAbsolutePath().toString())
        } catch (ex: Exception) {
            return null
        }
        logger.info("Loaded native library")

        return info.platformFactory()
    }

    val platform by lazy {
        load()
    }
}