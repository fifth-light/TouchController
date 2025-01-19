package top.fifthlight.touchcontroller.platform

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import top.fifthlight.touchcontroller.gal.NativeLibraryPathGetter
import top.fifthlight.touchcontroller.platform.android.AndroidPlatform
import top.fifthlight.touchcontroller.platform.proxy.ProxyPlatform
import top.fifthlight.touchcontroller.platform.win32.Win32Platform
import top.fifthlight.touchcontroller.proxy.server.localhostLauncherSocketProxyServer
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.DosFileAttributeView
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFilePermission
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.fileAttributesView
import kotlin.io.path.outputStream

object PlatformProvider : KoinComponent {
    private val nativeLibraryPathGetter: NativeLibraryPathGetter by inject()

    private val logger = LoggerFactory.getLogger(PlatformProvider::class.java)

    private val isAndroid: Boolean by lazy {
        // Detect the existence of /system/build.prop
        val path = Paths.get("/", "system", "build.prop")
        try {
            path.exists()
        } catch (ex: SecurityException) {
            logger.info("Failed to access $path, may running on Android", ex)
            true
        } catch (ex: IOException) {
            logger.info("Failed to access $path, may running on Android", ex)
            true
        }
    }

    private fun extractNativeLibrary(prefix: String, suffix: String, stream: InputStream): Path =
        stream.use { input ->
            Files.createTempFile(prefix, suffix).also { outputFile ->
                logger.info("Extracting native library to $outputFile")
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

    private data class NativeLibraryInfo(
        val modContainerName: String,
        val modContainerPath: String,
        val debugPath: Path?,
        val extractPrefix: String,
        val extractSuffix: String,
        val readOnlySetter: (Path) -> Unit = {},
        val removeAfterLoaded: Boolean,
        val platformFactory: () -> Platform
    )

    private fun windowsReadOnlySetter(path: Path) {
        val attributeView = path.fileAttributesView<DosFileAttributeView>()
        attributeView.setReadOnly(true)
    }

    private fun posixReadOnlySetter(path: Path) {
        val attributeView = path.fileAttributesView<PosixFileAttributeView>()
        // 500
        attributeView.setPermissions(
            setOf(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_EXECUTE
            )
        )
    }

    private fun load(): Platform? {
        val socketPort = System.getenv("TOUCH_CONTROLLER_PROXY")?.toIntOrNull()
        if (socketPort != null) {
            logger.warn("TOUCH_CONTROLLER_PROXY set, use legacy UDP transport")
            val proxy = localhostLauncherSocketProxyServer(socketPort) ?: return null
            return ProxyPlatform(proxy)
        }

        val systemName = System.getProperty("os.name")
        val systemArch = System.getProperty("os.arch")
        logger.info("System name: $systemName, system arch: $systemArch")

        val info = if (systemName.startsWith("Windows")) {
            // Windows
            val targetArch = when (systemArch) {
                "x86_32", "x86", "i386", "i486", "i586", "i686" -> "i686-pc-windows-gnullvm"
                "amd64", "x86_64" -> "x86_64-pc-windows-gnullvm"
                "arm64", "aarch64" -> "aarch64-pc-windows-gnullvm"
                else -> null
            } ?: run {
                logger.warn("Unsupported Windows arch")
                return null
            }
            logger.info("Target arch: $targetArch")

            NativeLibraryInfo(
                modContainerName = "top_fifthlight_touchcontroller_proxy-windows",
                modContainerPath = "$targetArch/proxy_windows.dll",
                debugPath = Paths.get("..", "..", "..", "target", targetArch, "release", "proxy_windows.dll"),
                extractPrefix = "proxy_windows",
                extractSuffix = ".dll",
                readOnlySetter = ::windowsReadOnlySetter,
                removeAfterLoaded = false,
                platformFactory = ::Win32Platform
            )
        } else if (systemName.startsWith("Linux")) {
            if (isAndroid) {
                logger.info("Android detected")

                val socketName = System.getenv("TOUCH_CONTROLLER_PROXY_SOCKET")?.takeIf { it.isNotEmpty() }
                if (socketName == null) {
                    logger.info("No TOUCH_CONTROLLER_PROXY_SOCKET environment set, TouchController will not be loaded")
                    return null
                }

                val targetArch = when (systemArch) {
                    "x86_32", "x86", "i386", "i486", "i586", "i686" -> "i686-linux-android"
                    "amd64", "x86_64" -> "x86_64-linux-android"
                    "armeabi", "armeabi-v7a", "armhf", "arm", "armel" -> "armv7-linux-androideabi"
                    "arm64", "aarch64" -> "aarch64-linux-android"
                    else -> null
                } ?: run {
                    logger.warn("Unsupported Android arch")
                    return null
                }

                NativeLibraryInfo(
                    modContainerName = "top_fifthlight_touchcontroller_proxy-server-android",
                    modContainerPath = "$targetArch/libproxy_server_android.so",
                    debugPath = null,
                    extractPrefix = "libproxy_server_android",
                    extractSuffix = ".so",
                    readOnlySetter = ::posixReadOnlySetter,
                    removeAfterLoaded = true,
                    platformFactory = {
                        AndroidPlatform(socketName)
                    }
                )
            } else {
                logger.warn("Linux is not supported for now!")
                return null
            }
        } else {
            logger.warn("Unsupported system: $systemName")
            return null
        }

        logger.info("Native library info:")
        logger.info("containerName: ${info.modContainerName}")
        logger.info("containerPath: ${info.modContainerPath}")
        val nativeLibrary = nativeLibraryPathGetter.getNativeLibraryPath(
            containerName = info.modContainerName,
            containerPath = info.modContainerPath,
            debugPath = info.debugPath
        ) ?: run {
            logger.warn("Failed to get native library path")
            return null
        }

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
            @Suppress("UnsafeDynamicallyLoadedCode")
            System.load(destinationFile.toAbsolutePath().toString())
        } catch (ex: Exception) {
            return null
        }
        logger.info("Loaded native library")

        if (info.removeAfterLoaded) {
            destinationFile.deleteIfExists()
        }

        return info.platformFactory()
    }

    val platform by lazy {
        load()
    }
}