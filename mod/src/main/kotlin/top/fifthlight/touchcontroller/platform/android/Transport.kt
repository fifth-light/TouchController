package top.fifthlight.touchcontroller.platform.android

object Transport {
    external fun new(name: String): Long
    external fun receive(handle: Long): ByteArray?
    external fun destroy(handle: Long)
}
