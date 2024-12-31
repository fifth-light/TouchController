package top.fifthlight.combine.platform

import net.minecraft.client.Minecraft
import top.fifthlight.combine.node.CombineCoroutineDispatcher
import kotlin.coroutines.CoroutineContext

class GameDispatcherImpl(private val client: Minecraft) : CombineCoroutineDispatcher() {
    private val dispatchQueue = ArrayDeque<Runnable>()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatchQueue.add(block)
    }

    override fun isDispatchNeeded(context: CoroutineContext): Boolean = client.isSameThread

    override fun execute() {
        while (true) {
            val task = dispatchQueue.removeFirstOrNull() ?: break
            task.run()
        }
    }
}