package top.fifthlight.touchcontroller.platform.android

object Transport {
    external fun new(name: String): Long
    external fun receive(handle: Long, buffer: ByteArray): Int
    external fun destroy(handle: Long)
}
