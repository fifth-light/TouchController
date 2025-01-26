package top.fifthlight.touchcontroller.gal

import org.slf4j.LoggerFactory
import java.io.InputStream
import java.nio.file.Path

object NativeLibraryPathGetterImpl : NativeLibraryPathGetter {
    private val logger = LoggerFactory.getLogger(NativeLibraryPathGetterImpl::class.java)

    override fun getNativeLibraryPath(path: String, debugPath: Path?): InputStream? {
        // TODO is debug or release?
        return javaClass.classLoader.getResourceAsStream(path) ?: run {
            logger.warn("Failed to get resource $path")
            null
        }
    }
}