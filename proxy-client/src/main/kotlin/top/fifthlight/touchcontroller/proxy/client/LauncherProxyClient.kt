package top.fifthlight.touchcontroller.proxy.client

import top.fifthlight.touchcontroller.proxy.data.Offset
import top.fifthlight.touchcontroller.proxy.message.AddPointerMessage
import top.fifthlight.touchcontroller.proxy.message.ClearPointerMessage
import top.fifthlight.touchcontroller.proxy.message.RemovePointerMessage
import top.fifthlight.touchcontroller.proxy.message.VibrateMessage
import java.util.concurrent.atomic.AtomicBoolean

class LauncherProxyClient(
    transport: MessageTransport
) : AutoCloseable {
    private val running = AtomicBoolean(false)
    private val messageClient = LauncherProxyMessageClient(transport)

    interface VibrationHandler {
        fun viberate(kind: VibrateMessage.Kind)
    }

    var vibrationHandler: VibrationHandler? = null

    fun run() {
        if (running.compareAndSet(false, true)) {
            messageClient.run()
            Thread {
                while (true) {
                    val message = messageClient.receive() ?: break
                    when (message) {
                        is VibrateMessage -> vibrationHandler?.viberate(message.kind)
                        else -> {
                            // Ignore
                        }
                    }
                }
            }.start()
        }
    }

    fun addPointer(index: Int, x: Float, y: Float) = addPointer(index, Offset(x, y))

    fun addPointer(index: Int, offset: Offset) {
        messageClient.send(AddPointerMessage(index = index, position = offset))
    }

    fun removePointer(index: Int) {
        messageClient.send(RemovePointerMessage(index = index))
    }

    fun clearPointer() {
        messageClient.send(ClearPointerMessage)
    }

    override fun close() {
        messageClient.close()
    }
}