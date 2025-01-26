package top.fifthlight.touchcontroller.gal

import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.inputStream

object NativeLibraryPathGetterImpl : NativeLibraryPathGetter {
    private val logger = LoggerFactory.getLogger(NativeLibraryPathGetterImpl::class.java)

    override fun getNativeLibraryPath(path: String, debugPath: Path?): InputStream? {
        val loader = FabricLoader.getInstance()
        return try {
            if (!loader.isDevelopmentEnvironment) {
                javaClass.classLoader.getResourceAsStream(path)
            } else {
                debugPath?.inputStream() ?: run {
                    logger.warn("No debug library for your platform")
                    null
                }
            }
        } catch (ex: IOException) {
            logger.warn("Open native library failed", ex)
            null
        }
    }
}