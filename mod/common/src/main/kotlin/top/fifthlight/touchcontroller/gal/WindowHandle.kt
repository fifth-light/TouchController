package top.fifthlight.touchcontroller.gal

import top.fifthlight.data.IntSize
import top.fifthlight.data.Offset

interface WindowHandle {
    val size: IntSize
    val scaledSize: IntSize
    val mouseLeftPressed: Boolean
    val mousePosition: Offset?
}

interface WindowHandleFactory {
    val currentWindow: WindowHandle
}