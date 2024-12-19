package top.fifthlight.touchcontroller.proxy.client

import top.fifthlight.touchcontroller.proxy.message.MessageDecodeException
import top.fifthlight.touchcontroller.proxy.message.ProxyMessage
import java.nio.ByteBuffer
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean

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
    private val receiveQueue = LinkedBlockingQueue<MessageItem>()
    private var running = AtomicBoolean(false)
    private var closed = AtomicBoolean(false)

    /**
     * 开始发送消息，会新建线程用于处理，不会阻塞当前线程。
     */
    fun run() {
        if (!running.compareAndSet(false, true)) {
            return
        }
        Thread {
            // Send thread
            val buffer = ByteBuffer.allocate(256)
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
            transport.close()
        }.start()
        Thread {
            // Receive thread
            val buffer = ByteBuffer.allocate(256)
            while (transport.receive(buffer)) {
                buffer.flip()

                if (buffer.remaining() < 4) {
                    // Message without type
                    buffer.clear()
                    continue
                }
                val type = buffer.getInt()
                try {
                    val message = ProxyMessage.decode(type, buffer)
                    receiveQueue.offer(MessageItem.Message(message))
                } catch (ex: MessageDecodeException) {
                    // Ignore bad message
                }

                buffer.clear()
            }
        }.start()
    }

    /**
     * 发送一个数据包。这个方法将数据包放到消息队列中，会立即返回，不会产生阻塞。
     *
     * @param message 要发送的数据包
     */
    fun send(message: ProxyMessage) {
        if (!closed.get()) {
            sendQueue.offer(MessageItem.Message(message))
        }
    }

    /**
     * 接收一个数据包。这个方法会阻塞直到接收到数据包或者被关闭为止。
     *
     * @return 接收到的数据包，如果当前  LauncherProxyMessageClient 被关闭则会返回 null。
     */
    fun receive(): ProxyMessage? {
        if (closed.get()) {
            return null
        }
        return when (val item = receiveQueue.take() ?: return null) {
            is MessageItem.Message -> item.message
            MessageItem.Close -> null
        }
    }

    /**
     * 关闭这个对象。关闭后消息队列中仍有的消息依然会尝试发送，但是新消息不会再被发送。
     */
    override fun close() {
        if (closed.compareAndSet(false, true)) {
            sendQueue.offer(MessageItem.Close)
            receiveQueue.offer(MessageItem.Close)
        }
    }
}
