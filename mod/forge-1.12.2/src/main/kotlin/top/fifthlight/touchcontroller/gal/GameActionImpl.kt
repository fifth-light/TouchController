package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiChat
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.platform.toMinecraft

object GameActionImpl : GameAction {
    private val client = Minecraft.getMinecraft()

    override fun openChatScreen() {
        client.displayGuiScreen(GuiChat());
    }

    override fun openGameMenu() {
        client.displayInGameMenu()
    }

    override fun sendMessage(text: Text) {
        client.ingameGUI.chatGUI.printChatMessage(text.toMinecraft())
    }
}