package top.fifthlight.touchcontroller.proxy.client

import java.nio.ByteBuffer

/**
 * 发送消息的底层运输层。
 */
interface MessageTransport {
    /**
     * 发送一个消息，可以堵塞线程。
     *
     * @param buffer 要发送的数据
     */
    fun send(buffer: ByteBuffer)
}