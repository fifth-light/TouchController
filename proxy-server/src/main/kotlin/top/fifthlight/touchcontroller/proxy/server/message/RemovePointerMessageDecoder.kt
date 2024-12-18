package top.fifthlight.touchcontroller.proxy.server.message

import top.fifthlight.touchcontroller.proxy.message.RemovePointerMessage
import java.nio.ByteBuffer

object RemovePointerMessageDecoder : ProxyMessageDecoder() {
    override fun decode(payload: ByteBuffer): RemovePointerMessage {
        if (payload.remaining() != 4) {
            throw BadMessageLengthException(
                expected = 4,
                actual = payload.remaining()
            )
        }
        return RemovePointerMessage(
            index = payload.getInt(),
        )
    }
}