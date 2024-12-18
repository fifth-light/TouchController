package top.fifthlight.touchcontroller.proxy.client

import top.fifthlight.touchcontroller.proxy.data.Offset
import top.fifthlight.touchcontroller.proxy.message.AddPointerMessage
import top.fifthlight.touchcontroller.proxy.message.ClearPointerMessage
import top.fifthlight.touchcontroller.proxy.message.RemovePointerMessage

class LauncherProxyClient(transport: MessageTransport) : AutoCloseable {
    private val messageClient = LauncherProxyMessageClient(transport)

    fun run() {
        messageClient.run()
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