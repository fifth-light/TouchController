package top.fifthlight.touchcontroller.proxy.message

import top.fifthlight.touchcontroller.proxy.data.Offset
import java.nio.ByteBuffer

data class AddPointerMessage(
    val index: Int,
    val position: Offset
): ProxyMessage() {
    override val type: Int = 1

    override fun encode(buffer: ByteBuffer) {
        super.encode(buffer)
        buffer.putInt(index)
        buffer.putFloat(position.x)
        buffer.putFloat(position.y)
    }

    object Decoder : ProxyMessageDecoder<AddPointerMessage>() {
        override fun decode(payload: ByteBuffer): AddPointerMessage {
            if (payload.remaining() != 12) {
                throw BadMessageLengthException(
                    expected = 12,
                    actual = payload.remaining()
                )
            }
            return AddPointerMessage(
                index = payload.getInt(),
                position = Offset(
                    x = payload.getFloat(),
                    y = payload.getFloat()
                )
            )
        }
    }
}