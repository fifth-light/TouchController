package top.fifthlight.touchcontroller.platform.proxy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import top.fifthlight.touchcontroller.platform.Platform
import top.fifthlight.touchcontroller.proxy.message.ProxyMessage
import top.fifthlight.touchcontroller.proxy.server.LauncherSocketProxyServer

class ProxyPlatform(private val proxy: LauncherSocketProxyServer) : Platform {
    override fun init(scope: CoroutineScope) {
        scope.launch {
            proxy.start()
        }
    }

    override fun pollEvent(): ProxyMessage? = proxy.receive()
}