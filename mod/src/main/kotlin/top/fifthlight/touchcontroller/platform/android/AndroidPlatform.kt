package top.fifthlight.touchcontroller.platform.android

import top.fifthlight.touchcontroller.platform.Platform
import top.fifthlight.touchcontroller.proxy.message.ProxyMessage

class AndroidPlatform : Platform {
    override suspend fun pollEvent(): ProxyMessage? = null
}