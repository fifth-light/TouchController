package top.fifthlight.touchcontroller.proxy.client

import java.nio.ByteBuffer

/**
 * 发送消息的底层运输层。
 */
interface MessageTransport : AutoCloseable {
    /**
     * 发送一个消息，可以阻塞线程。
     *
     * @param buffer 要发送的数据
     */
    fun send(buffer: ByteBuffer)

    /**
     * 读取一个消息，可以阻塞线程。如果不支持读取数据，则该方法应该阻塞直到关闭为止。
     *
     * @param buffer 要读取的数据缓冲区
     * @return 是否读取到数据，仅在该对象被 close() 时返回 false
     */
    fun receive(buffer: ByteBuffer): Boolean
}
