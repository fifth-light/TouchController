package top.fifthlight.touchcontroller.gal

import net.minecraft.client.MinecraftClient
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.platform.toMinecraft
import top.fifthlight.touchcontroller.mixin.ClientOpenChatScreenInvoker

object GameActionImpl : GameAction {
    private val client = MinecraftClient.getInstance()

    override fun openChatScreen() {
        (client as ClientOpenChatScreenInvoker).callOpenChatScreen("")
    }

    override fun openGameMenu() {
        client.openPauseMenu(false)
    }

    override fun sendMessage(text: Text) {
        client.inGameHud.chatHud.addMessage(text.toMinecraft())
    }
}