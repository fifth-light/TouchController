package top.fifthlight.touchcontroller.handler

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.MinecraftClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import top.fifthlight.touchcontroller.platform.PlatformHolder

class ClientStartHandler : ClientLifecycleEvents.ClientStarted, KoinComponent {
    private val logger = LoggerFactory.getLogger(ClientStartHandler::class.java)

    private val platform: PlatformHolder by inject()

    override fun onClientStarted(client: MinecraftClient) {
        val platform = platform.platform ?: return
        val window = client.window
        platform.onWindowCreated(window)
        logger.info("Called platform onWindowCreated")
    }
}
