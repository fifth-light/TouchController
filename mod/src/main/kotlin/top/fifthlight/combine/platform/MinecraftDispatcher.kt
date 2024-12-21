package top.fifthlight.combine.platform

import net.minecraft.client.MinecraftClient
import top.fifthlight.combine.node.CombineCoroutineDispatcher
import kotlin.coroutines.CoroutineContext

class MinecraftDispatcher(private val client: MinecraftClient): CombineCoroutineDispatcher() {
    private val dispatchQueue = ArrayDeque<Runnable>()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatchQueue.add(block)
    }

    override fun isDispatchNeeded(context: CoroutineContext): Boolean = client.isOnThread

    override fun execute() {
        while (true) {
            val task = dispatchQueue.removeFirstOrNull() ?: break
            task.run()
        }
    }
}