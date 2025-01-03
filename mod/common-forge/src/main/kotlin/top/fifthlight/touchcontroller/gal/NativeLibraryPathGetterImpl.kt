package top.fifthlight.touchcontroller.gal

import net.minecraftforge.fml.loading.FMLEnvironment
import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.io.path.toPath

object NativeLibraryPathGetterImpl : NativeLibraryPathGetter {
    private val logger = LoggerFactory.getLogger(NativeLibraryPathGetterImpl::class.java)

    override fun getNativeLibraryPath(containerName: String, containerPath: String, debugPath: Path?): Path? {
        if (FMLEnvironment.production) {
            javaClass.classLoader.getResource(containerPath)?.toURI()?.toPath()
        } else {
            debugPath ?: run {
                logger.warn("No debug library for your platform")
                null
            }
        }
        return null
    }
}