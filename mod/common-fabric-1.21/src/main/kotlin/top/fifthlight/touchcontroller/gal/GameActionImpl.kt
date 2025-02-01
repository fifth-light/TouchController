package top.fifthlight.touchcontroller.gal

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.ScreenshotRecorder
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.platform.toMinecraft
import top.fifthlight.touchcontroller.mixin.ClientOpenChatScreenInvoker

object GameActionImpl : GameAction {
    private val client = MinecraftClient.getInstance()

    override fun openChatScreen() {
        (client as ClientOpenChatScreenInvoker).callOpenChatScreen("")
    }

    override fun openGameMenu() {
        client.openGameMenu(false)
    }

    override fun sendMessage(text: Text) {
        client.inGameHud.chatHud.addMessage(text.toMinecraft())
    }

    override fun nextPerspective() {
        val perspective = client.options.perspective
        client.options.perspective = client.options.perspective.next()
        if (perspective.isFirstPerson != client.options.perspective.isFirstPerson) {
            val newCameraEntity = client.getCameraEntity().takeIf { client.options.perspective.isFirstPerson }
            client.gameRenderer.onCameraEntitySet(newCameraEntity)
        }
        client.worldRenderer.scheduleTerrainUpdate()
    }

    override fun takeScreenshot() {
        ScreenshotRecorder.saveScreenshot(
            client.runDirectory,
            client.framebuffer,
        ) { message ->
            this.client.execute {
                this.client.inGameHud.chatHud.addMessage(message)
            }
        }
    }

    override fun takePanorama() {
        client.takePanorama(
            client.runDirectory,
            client.window.width,
            client.window.width,
        ).let { message ->
            this.client.execute {
                this.client.inGameHud.chatHud.addMessage(message)
            }
        }
    }
}