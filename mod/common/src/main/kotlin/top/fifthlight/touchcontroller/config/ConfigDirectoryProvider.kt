package top.fifthlight.touchcontroller.config

import java.nio.file.Path

interface ConfigDirectoryProvider {
    fun getConfigDirectory(): Path
}