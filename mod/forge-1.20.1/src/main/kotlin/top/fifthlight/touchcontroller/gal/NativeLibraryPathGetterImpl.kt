package top.fifthlight.touchcontroller.gal

import net.minecraftforge.fml.loading.FMLEnvironment
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.inputStream

object NativeLibraryPathGetterImpl : NativeLibraryPathGetter {
    private val logger = LoggerFactory.getLogger(NativeLibraryPathGetterImpl::class.java)

    override fun getNativeLibraryPath(path: String, debugPath: Path?): InputStream? {
        return if (FMLEnvironment.production) {
            javaClass.classLoader.getResourceAsStream(path) ?: run {
                logger.warn("Failed to get resource $path")
                null
            }
        } else {
            try {
                debugPath?.inputStream()
            } catch (ex: IOException) {
                logger.warn("Read debug library failed", ex)
                null
            } ?: run {
                logger.warn("No debug library for your platform")
                null
            }
        }
    }
}