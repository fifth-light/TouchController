package top.fifthlight.touchcontroller.platform

import kotlinx.coroutines.CoroutineScope
import top.fifthlight.touchcontroller.gal.PlatformWindow
import top.fifthlight.touchcontroller.proxy.message.ProxyMessage

// Workaround for Koin to pass nullable values
data class PlatformHolder(
    var platform: Platform?
)

interface Platform {
    fun init(scope: CoroutineScope) {}
    fun onWindowCreated(window: PlatformWindow) {}
    fun pollEvent(): ProxyMessage?
    fun sendEvent(message: ProxyMessage)
}