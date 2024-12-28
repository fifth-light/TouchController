package top.fifthlight.touchcontroller.event

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.gal.GameAction
import top.fifthlight.touchcontroller.platform.PlatformHolder
import top.fifthlight.touchcontroller.platform.proxy.ProxyPlatform

object ConnectionEvents : KoinComponent {
    private val platformHolder: PlatformHolder by inject()
    private val gameAction: GameAction by inject()
    private val textFactory: TextFactory by inject()

    fun onJoinedWorld() {
        val platform = platformHolder.platform
        if (platform == null) {
            gameAction.sendMessage(textFactory.of(Texts.WARNING_PROXY_NOT_CONNECTED))
        } else if (platform is ProxyPlatform) {
            gameAction.sendMessage(textFactory.of(Texts.WARNING_LEGACY_UDP_PROXY_USED))
        }
    }
}