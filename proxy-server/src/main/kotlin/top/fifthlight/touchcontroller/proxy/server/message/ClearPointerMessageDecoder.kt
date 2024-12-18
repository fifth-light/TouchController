package top.fifthlight.touchcontroller.proxy.server.message

import top.fifthlight.touchcontroller.proxy.message.ClearPointerMessage
import java.nio.ByteBuffer

object ClearPointerMessageDecoder : ProxyMessageDecoder() {
    override fun decode(payload: ByteBuffer): ClearPointerMessage {
        if (payload.hasRemaining()) {
            throw BadMessageLengthException(
                expected = 0,
                actual = payload.remaining()
            )
        }
        return ClearPointerMessage
    }
}