package top.fifthlight.touchcontroller.handler

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.asset.Texts
import top.fifthlight.touchcontroller.platform.PlatformHolder

class ClientPlayConnectionHandler : ClientPlayConnectionEvents.Join, KoinComponent {
    private val platform: PlatformHolder by inject()

    override fun onPlayReady(handler: ClientPlayNetworkHandler, sender: PacketSender, client: MinecraftClient) {
        if (platform.platform == null) {
            client.inGameHud.chatHud.addMessage(Texts.WARNING_PROXY_NOT_CONNECTED)
        }
    }
}