package top.fifthlight.touchcontroller.platform.android

import org.slf4j.LoggerFactory
import top.fifthlight.touchcontroller.platform.Platform
import top.fifthlight.touchcontroller.proxy.message.MessageDecodeException
import top.fifthlight.touchcontroller.proxy.message.ProxyMessage
import java.nio.ByteBuffer

class AndroidPlatform(name: String) : Platform {
    private val logger = LoggerFactory.getLogger(AndroidPlatform::class.java)

    private val handle = Transport.new(name)

    private val readBuffer = ByteArray(128)
    override fun pollEvent(): ProxyMessage? {
        val receivedLength = Transport.receive(handle, readBuffer)
        val length = receivedLength.takeIf { it > 0 } ?: return null
        val buffer = ByteBuffer.wrap(readBuffer)
        buffer.limit(length)
        if (buffer.remaining() < 4) {
            return null
        }
        val type = buffer.getInt()
        return try {
            ProxyMessage.decode(type, buffer)
        } catch (ex: MessageDecodeException) {
            logger.warn("Bad message: $ex")
            null
        }
    }

    override fun sendEvent(message: ProxyMessage) {
        val buffer = ByteBuffer.allocate(256)
        message.encode(buffer)
        buffer.flip()
        Transport.send(handle, buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining())
    }
}
