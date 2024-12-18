package top.fifthlight.touchcontroller.proxy.server.message

import top.fifthlight.touchcontroller.proxy.data.Offset
import top.fifthlight.touchcontroller.proxy.message.AddPointerMessage
import java.nio.ByteBuffer

object AddPointerMessageDecoder : ProxyMessageDecoder() {
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