package top.fifthlight.touchcontroller.gal

import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.io.path.toPath

object NativeLibraryPathGetterImpl : NativeLibraryPathGetter {
    private val logger = LoggerFactory.getLogger(NativeLibraryPathGetterImpl::class.java)

    override fun getNativeLibraryPath(containerName: String, containerPath: String, debugPath: Path?): Path? {
        // TODO is debug or release?
        return javaClass.classLoader.getResource("/$containerPath")?.toURI()?.toPath() ?: run {
            logger.warn("Failed to get resource $containerPath")
            null
        }
    }
}