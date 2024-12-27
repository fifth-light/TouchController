package top.fifthlight.touchcontroller.platform.win32

import org.slf4j.LoggerFactory
import top.fifthlight.touchcontroller.platform.Platform
import top.fifthlight.touchcontroller.platform.PlatformWindow
import top.fifthlight.touchcontroller.proxy.message.MessageDecodeException
import top.fifthlight.touchcontroller.proxy.message.ProxyMessage
import java.nio.ByteBuffer

class Win32Platform: Platform {
    private val logger = LoggerFactory.getLogger(Win32Platform::class.java)

    override fun onWindowCreated(window: PlatformWindow) {
        val handle = window.getWin32Handle()
        Interface.init(handle)
    }

    private val readBuffer = ByteArray(128)
    override fun pollEvent(): ProxyMessage? {
        val length = Interface.pollEvent(readBuffer).takeIf { it != 0 } ?: return null
        val buffer = ByteBuffer.wrap(readBuffer)
        buffer.limit(length)
        if (buffer.remaining() < 4) {
            return null
        }
        val type = buffer.getInt()
        return try {
            ProxyMessage.decode(type, buffer)
        } catch (ex: MessageDecodeException) {
            logger.warn("Bad message from native side: $ex")
            null
        }
    }

    override fun sendEvent(message: ProxyMessage) {
        // Win32 don't support vibration for now
    }
}