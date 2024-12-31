package top.fifthlight.touchcontroller.gal

import org.slf4j.LoggerFactory
import java.nio.file.Path

object NativeLibraryPathGetterImpl : NativeLibraryPathGetter {
    private val logger = LoggerFactory.getLogger(NativeLibraryPathGetterImpl::class.java)

    override fun getNativeLibraryPath(containerName: String, containerPath: String, debugPath: Path?): Path? {
        // TODO
        logger.warn("TODO getNativeLibraryPath()")
        return null
    }
}