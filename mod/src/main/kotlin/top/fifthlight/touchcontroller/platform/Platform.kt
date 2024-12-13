package top.fifthlight.touchcontroller.platform

import kotlinx.coroutines.CoroutineScope
import net.minecraft.client.util.Window
import top.fifthlight.touchcontroller.proxy.message.ProxyMessage

interface Platform {
    suspend fun init(scope: CoroutineScope) {}
    fun onWindowCreated(window: Window) {}
    suspend fun pollEvent(): ProxyMessage?
}