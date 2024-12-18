package top.fifthlight.touchcontroller.proxy.message

import java.nio.ByteBuffer

data class RemovePointerMessage(
    val index: Int
): ProxyMessage() {
    override val type: Int = 2

    override fun encode(buffer: ByteBuffer) {
        super.encode(buffer)
        buffer.putInt(index)
    }
}