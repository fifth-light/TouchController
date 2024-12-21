package top.fifthlight.touchcontroller.proxy.client

import top.fifthlight.data.Offset
import top.fifthlight.touchcontroller.proxy.message.AddPointerMessage
import top.fifthlight.touchcontroller.proxy.message.ClearPointerMessage
import top.fifthlight.touchcontroller.proxy.message.RemovePointerMessage
import top.fifthlight.touchcontroller.proxy.message.VibrateMessage
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 与 TouchController 交互的接口。
 *
 * 创建好 LauncherProxyClient 对象后，必须要调用 run() 方法才会工作。
 *
 * @param transport 使用到的消息运输层
 */
class LauncherProxyClient(
    transport: MessageTransport
) : AutoCloseable {
    private val running = AtomicBoolean(false)
    private val messageClient = LauncherProxyMessageClient(transport)

    /**
     * 处理震动事件的处理器。
     */
    interface VibrationHandler {
        /**
         * 当震动事件发生时会调用这个方法。
         *
         * @param kind 震动事件的种类。
         */
        fun viberate(kind: VibrateMessage.Kind)
    }

    /**
     * 处理震动事件的处理器，为 null 时则会忽略震动事件。
     */
    var vibrationHandler: VibrationHandler? = null

    /**
     * 开始处理消息，会新建线程用于处理，不会阻塞当前线程。
     */
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

    /**
     * 添加或者移动一个指针。
     *
     * @param index 指针的序号。新指针的序号必须从一开始单调递增。
     * @param x 指针的 X 坐标，范围为相对游戏区域的 [0, 1]。
     * @param y 指针的 X 坐标，范围为相对游戏区域的 [0, 1]。
     */
    fun addPointer(index: Int, x: Float, y: Float) = addPointer(index, Offset(x, y))

    /**
     * 添加或者移动一个指针。
     *
     * @param index 指针的序号。新指针的序号必须从一开始单调递增。
     * @param offset 指针的坐标，范围为相对游戏区域的 [0, 1]。
     */
    fun addPointer(index: Int, offset: Offset) {
        messageClient.send(AddPointerMessage(index = index, position = offset))
    }

    /**
     * 移除一个指针。
     *
     * @param index 指针的序号。
     */
    fun removePointer(index: Int) {
        messageClient.send(RemovePointerMessage(index = index))
    }

    /**
     * 清除所有的指针。
     */
    fun clearPointer() {
        messageClient.send(ClearPointerMessage)
    }

    override fun close() {
        messageClient.close()
    }
}