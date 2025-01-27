package top.fifthlight.touchcontroller

import net.minecraftforge.fml.common.Loader
import top.fifthlight.touchcontroller.config.ConfigDirectoryProvider
import java.nio.file.Path

object ConfigDirectoryProviderImpl : ConfigDirectoryProvider {
    override fun getConfigDirectory(): Path {
        val fmlConfigPath = Loader.instance().configDir.toPath()
        return fmlConfigPath.resolve(BuildInfo.MOD_ID)
    }
}