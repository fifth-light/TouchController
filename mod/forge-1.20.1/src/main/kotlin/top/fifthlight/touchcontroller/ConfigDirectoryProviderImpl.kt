package top.fifthlight.touchcontroller

import net.minecraftforge.fml.loading.FMLPaths
import top.fifthlight.touchcontroller.config.ConfigDirectoryProvider
import java.nio.file.Path

object ConfigDirectoryProviderImpl : ConfigDirectoryProvider {
    override fun getConfigDirectory(): Path {
        val fmlConfigPath = FMLPaths.CONFIGDIR.get()
        return fmlConfigPath.resolve("touchcontroller")
    }
}