package top.fifthlight.touchcontroller.proxy.server.message

import top.fifthlight.touchcontroller.proxy.message.ProxyMessage
import java.nio.ByteBuffer

abstract class ProxyMessageDecoder {
    abstract fun decode(payload: ByteBuffer): ProxyMessage
}

abstract class MessageDecodeException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
class BadMessageTypeException(type: Int) : MessageDecodeException("Bad type: $type")
class BadMessageLengthException(expected: Int, actual: Int) :
    MessageDecodeException("Bad message length: expected $expected bytes, but got $actual bytes")

fun decodeMessage(type: Int, payload: ByteBuffer): ProxyMessage = when (type) {
    1 -> AddPointerMessageDecoder
    2 -> RemovePointerMessageDecoder
    3 -> ClearPointerMessageDecoder
    else -> throw BadMessageTypeException(type)
}.decode(payload)
