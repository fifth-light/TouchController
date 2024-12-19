package top.fifthlight.touchcontroller.proxy.message

import java.nio.ByteBuffer

class VibrateMessage(
    val kind: Kind
) : ProxyMessage() {
    override val type: Int = 4

    enum class Kind(val index: Int) {
        UNKNOWN(-1),
        BLOCK_BROKEN(0),
    }

    override fun encode(buffer: ByteBuffer) {
        super.encode(buffer)
        buffer.putInt(kind.index)
    }

    object Decoder : ProxyMessageDecoder<VibrateMessage>() {
        override fun decode(payload: ByteBuffer): VibrateMessage {
            if (payload.remaining() != 4) {
                throw BadMessageLengthException(
                    expected = 4,
                    actual = payload.remaining()
                )
            }
            return VibrateMessage(
                kind = when (payload.getInt()) {
                    0 -> Kind.BLOCK_BROKEN
                    else -> Kind.UNKNOWN
                }
            )
        }
    }
}
