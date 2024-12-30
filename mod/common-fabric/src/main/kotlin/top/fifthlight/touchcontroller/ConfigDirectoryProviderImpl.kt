package top.fifthlight.touchcontroller

import net.fabricmc.loader.api.FabricLoader
import top.fifthlight.touchcontroller.config.ConfigDirectoryProvider
import java.nio.file.Path

object ConfigDirectoryProviderImpl : ConfigDirectoryProvider {
    override fun getConfigDirectory(): Path {
        val fabricLoader = FabricLoader.getInstance()
        return fabricLoader.configDir.resolve("touchcontroller")
    }
}