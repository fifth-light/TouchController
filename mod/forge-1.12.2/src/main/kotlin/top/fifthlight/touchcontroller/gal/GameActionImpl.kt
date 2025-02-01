package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiChat
import net.minecraft.util.ScreenShotHelper
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.platform.toMinecraft

object GameActionImpl : GameAction {
    private val client = Minecraft.getMinecraft()

    override fun openChatScreen() {
        client.displayGuiScreen(GuiChat())
    }

    override fun openGameMenu() {
        client.displayInGameMenu()
    }

    override fun sendMessage(text: Text) {
        client.ingameGUI.chatGUI.printChatMessage(text.toMinecraft())
    }

    override fun nextPerspective() {
        client.gameSettings.thirdPersonView++
        if (client.gameSettings.thirdPersonView > 2) {
            client.gameSettings.thirdPersonView = 0
        }

        if (client.gameSettings.thirdPersonView == 0) {
            client.entityRenderer.loadEntityShader(client.renderViewEntity)
        } else if (client.gameSettings.thirdPersonView == 1) {
            client.entityRenderer.loadEntityShader(null)
        }

        client.renderGlobal.setDisplayListEntitiesDirty()
    }

    override fun takeScreenshot() {
        ScreenShotHelper.saveScreenshot(
            client.gameDir,
            client.displayWidth,
            client.displayHeight,
            client.framebuffer
        ).let { message ->
            client.ingameGUI.chatGUI.printChatMessage(message)
        }
    }
}