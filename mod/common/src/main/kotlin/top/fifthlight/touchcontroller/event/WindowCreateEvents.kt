package top.fifthlight.touchcontroller.event

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import top.fifthlight.touchcontroller.gal.PlatformWindow
import top.fifthlight.touchcontroller.platform.PlatformHolder

object WindowCreateEvents : KoinComponent {
    private val logger = LoggerFactory.getLogger(WindowCreateEvents::class.java)

    private val platform: PlatformHolder by inject()

    fun onPlatformWindowCreated(window: PlatformWindow) {
        val platform = platform.platform ?: return
        platform.onWindowCreated(window)
        logger.info("Called platform onWindowCreated")
    }
}