package top.fifthlight.touchcontroller.proxy.message

import java.nio.ByteBuffer

sealed class ProxyMessage {
    abstract val type: Int

    open fun encode(buffer: ByteBuffer) {
        buffer.putInt(type)
    }

    companion object {
        fun decode(type: Int, payload: ByteBuffer): ProxyMessage = when (type) {
            1 -> AddPointerMessage.Decoder
            2 -> RemovePointerMessage.Decoder
            3 -> ClearPointerMessage.Decoder
            4 -> VibrateMessage.Decoder
            else -> throw BadMessageTypeException(type)
        }.decode(payload)
    }
}

abstract class ProxyMessageDecoder<M : ProxyMessage> {
    abstract fun decode(payload: ByteBuffer): M
}

abstract class MessageDecodeException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
class BadMessageTypeException(type: Int) : MessageDecodeException("Bad type: $type")
class BadMessageLengthException(expected: Int, actual: Int) :
    MessageDecodeException("Bad message length: expected $expected bytes, but got $actual bytes")
