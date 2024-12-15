package top.fifthlight.touchcontroller.platform

import kotlinx.coroutines.CoroutineScope
import net.minecraft.client.util.Window
import top.fifthlight.touchcontroller.proxy.message.ProxyMessage

// Workaround for Koin to pass nullable values
data class PlatformHolder(
    val platform: Platform?
)

interface Platform {
    suspend fun init(scope: CoroutineScope) {}
    fun onWindowCreated(window: Window) {}
    suspend fun pollEvent(): ProxyMessage?
}