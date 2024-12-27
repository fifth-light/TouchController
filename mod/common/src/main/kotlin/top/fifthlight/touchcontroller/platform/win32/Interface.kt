package top.fifthlight.touchcontroller.platform.win32

object Interface {
    @JvmStatic
    external fun init(windowHandle: Long)

    @JvmStatic
    external fun pollEvent(buffer: ByteArray): Int
}