package top.fifthlight.touchcontroller.platform.android

object Transport {
    private external fun init()
    external fun new(name: String): Long
    external fun receive(handle: Long, buffer: ByteArray): Int
    external fun send(handle: Long, buffer: ByteArray, off: Int, len: Int)
    external fun destroy(handle: Long)

    init {
        init()
    }
}
