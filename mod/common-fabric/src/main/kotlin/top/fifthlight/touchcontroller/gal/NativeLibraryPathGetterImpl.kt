package top.fifthlight.touchcontroller.gal

import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.jvm.optionals.getOrNull

object NativeLibraryPathGetterImpl : NativeLibraryPathGetter {
    private val logger = LoggerFactory.getLogger(NativeLibraryPathGetterImpl::class.java)

    override fun getNativeLibraryPath(containerName: String, containerPath: String, debugPath: Path?): Path? {
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
}