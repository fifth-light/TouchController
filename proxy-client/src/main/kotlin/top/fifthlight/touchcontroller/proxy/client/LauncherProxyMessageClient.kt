package top.fifthlight.touchcontroller.proxy.client

import top.fifthlight.touchcontroller.proxy.message.ProxyMessage
import java.nio.ByteBuffer
import java.util.concurrent.LinkedBlockingQueue

/**
 * 与 TouchController 交互的底层接口。
 *
 * 这个类提供了对 TouchController 底层协议的使用，可以向 TouchController 发送自定义的数据包。
 * 如果只是想使用 TouchController 的高层功能，可以使用 LauncherProxyClient。
 * 调用 send() 方法可以发送数据包，需要调用 run() 方法来运行，否则消息会累积在消息队列中不会发送。
 *
 * @param transport 使用到的消息运输层
 */
class LauncherProxyMessageClient(private val transport: MessageTransport) : AutoCloseable {
    private sealed class MessageItem {
        data class Message(val message: ProxyMessage) : MessageItem()
        data object Close : MessageItem()
    }

    private val sendQueue = LinkedBlockingQueue<MessageItem>()
    private var running: Boolean = false
    private var closed: Boolean = false

    /**
     * 开始发送消息，会堵塞当前线程。
     */
    fun run() {
        if (running) {
            return
        }
        running = true
        val buffer = ByteBuffer.allocate(1024)
        while (true) {
            when (val item = sendQueue.take()) {
                is MessageItem.Close -> break
                is MessageItem.Message -> {
                    val message = item.message
                    buffer.clear()
                    message.encode(buffer)
                    buffer.flip()
                    transport.send(buffer)
                }
            }
        }
    }

    /**
     * 发送一个数据包。这个方法将数据包放到消息队列中，会立即返回，不会产生阻塞。
     *
     * @param message 要发送的数据包
     */
    fun send(message: ProxyMessage) {
        if (!closed) {
            sendQueue.offer(MessageItem.Message(message))
        }
    }

    /**
     * 关闭这个对象。关闭后消息队列中仍有的消息依然会尝试发送，但是新消息不会再被发送。
     */
    override fun close() {
        if (!closed) {
            closed = true
            sendQueue.offer(MessageItem.Close)
        }
    }
}
