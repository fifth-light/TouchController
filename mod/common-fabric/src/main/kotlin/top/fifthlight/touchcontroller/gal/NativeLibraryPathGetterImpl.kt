package top.fifthlight.touchcontroller.gal

import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.jvm.optionals.getOrNull

object NativeLibraryPathGetterImpl : NativeLibraryPathGetter {
    private val logger = LoggerFactory.getLogger(NativeLibraryPathGetterImpl::class.java)

    override fun getNativeLibraryPath(containerName: String, containerPath: String, debugPath: Path?): InputStream? {
        val loader = FabricLoader.getInstance()
        val path = if (!loader.isDevelopmentEnvironment) {
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
        return try {
            path?.inputStream()
        } catch (ex: IOException) {
            logger.warn("Open native library failed", ex)
            null
        }
    }
}