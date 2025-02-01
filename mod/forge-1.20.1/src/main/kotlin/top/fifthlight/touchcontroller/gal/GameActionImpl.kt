package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft
import net.minecraft.client.Screenshot
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.platform.toMinecraft
import top.fifthlight.touchcontroller.mixin.ClientOpenChatScreenInvoker

object GameActionImpl : GameAction {
    private val client = Minecraft.getInstance()

    override fun openChatScreen() {
        (client as ClientOpenChatScreenInvoker).callOpenChatScreen("")
    }

    override fun openGameMenu() {
        client.pauseGame(false)
    }

    override fun sendMessage(text: Text) {
        client.gui.chat.addMessage(text.toMinecraft())
    }

    override fun nextPerspective() {
        val perspective = client.options.cameraType
        client.options.cameraType = client.options.cameraType.cycle()
        if (perspective.isFirstPerson != client.options.cameraType.isFirstPerson) {
            val newCameraEntity = client.getCameraEntity().takeIf { client.options.cameraType.isFirstPerson }
            client.gameRenderer.checkEntityPostEffect(newCameraEntity)
        }
    }

    override fun takeScreenshot() {
        Screenshot.grab(
            client.gameDirectory,
            client.mainRenderTarget,
        ) { message ->
            this.client.execute {
                this.client.gui.chat.addMessage(message)
            }
        }
    }
}