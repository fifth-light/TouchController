package top.fifthlight.touchcontroller.platform

import kotlinx.coroutines.CoroutineScope
import top.fifthlight.touchcontroller.proxy.message.ProxyMessage

// Workaround for Koin to pass nullable values
data class PlatformHolder(
    val platform: Platform?
)

interface PlatformWindow {
    fun getWin32Handle(): Long
}

interface Platform {
    fun init(scope: CoroutineScope) {}
    fun onWindowCreated(window: PlatformWindow) {}
    fun pollEvent(): ProxyMessage?
    fun sendEvent(message: ProxyMessage)
}