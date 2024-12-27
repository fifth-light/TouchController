package top.fifthlight.touchcontroller.platform

import java.nio.file.Path

interface NativeLibraryPathGetter {
    fun getNativeLibraryPath(containerName: String, containerPath: String, debugPath: Path?): Path?
}