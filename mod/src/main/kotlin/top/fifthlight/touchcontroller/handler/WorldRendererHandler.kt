package top.fifthlight.touchcontroller.handler

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BeforeBlockOutline
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.data.Offset
import top.fifthlight.touchcontroller.config.TouchControllerConfigHolder
import top.fifthlight.touchcontroller.event.HudRenderCallback
import top.fifthlight.touchcontroller.model.ControllerHudModel
import top.fifthlight.touchcontroller.model.GlobalStateModel
import top.fifthlight.touchcontroller.model.TouchStateModel
import top.fifthlight.touchcontroller.platform.PlatformHolder
import top.fifthlight.touchcontroller.proxy.message.AddPointerMessage
import top.fifthlight.touchcontroller.proxy.message.ClearPointerMessage
import top.fifthlight.touchcontroller.proxy.message.RemovePointerMessage
import top.fifthlight.touchcontroller.proxy.message.VibrateMessage

class WorldRendererHandler : WorldRenderEvents.Start, BeforeBlockOutline, HudRenderCallback.CrosshairRender,
    KoinComponent {
    private val platformHolder: PlatformHolder by inject()
    private val touchStateModel: TouchStateModel by inject()
    private val globalStateModel: GlobalStateModel by inject()
    private val controllerHudModel: ControllerHudModel by inject()
    private val configHolder: TouchControllerConfigHolder by inject()
    private val client: MinecraftClient by inject()

    override fun beforeBlockOutline(context: WorldRenderContext, hitResult: HitResult?): Boolean =
        controllerHudModel.result.crosshairStatus != null

    override fun onCrosshairRender(drawContext: DrawContext, tickCounter: RenderTickCounter): Boolean {
        val config = configHolder.config.value
        if (!config.disableCrosshair) {
            return true
        }
        return client.player?.let { player ->
            for (hand in Hand.entries) {
                val stack = player.getStackInHand(hand)
                if (stack.item in config.showCrosshairItems) {
                    return@let true
                }
            }
            false
        } ?: false
    }

    override fun onStart(context: WorldRenderContext) {
        globalStateModel.update(client)

        if (controllerHudModel.status.vibrate) {
            platformHolder.platform?.sendEvent(VibrateMessage(VibrateMessage.Kind.BLOCK_BROKEN))
            controllerHudModel.status.vibrate = false
        }

        platformHolder.platform?.let { platform ->
            while (true) {
                val message = platform.pollEvent() ?: break
                when (message) {
                    is AddPointerMessage -> {
                        touchStateModel.addPointer(
                            index = message.index,
                            position = Offset(
                                x = message.x,
                                y = message.y,
                            )
                        )
                    }

                    is RemovePointerMessage -> {
                        touchStateModel.removePointer(message.index)
                    }

                    ClearPointerMessage -> touchStateModel.clearPointer()

                    else -> {}
                }
            }
        }

        val config = configHolder.config.value
        if (config.enableTouchEmulation) {
            val mouse = client.mouse
            if (mouse.wasLeftButtonClicked()) {
                val mousePosition = Offset(
                    x = (mouse.x / client.window.width).toFloat(),
                    y = (mouse.y / client.window.height).toFloat()
                )
                touchStateModel.addPointer(
                    index = 0,
                    position = mousePosition
                )
            } else {
                touchStateModel.clearPointer()
            }
        }
    }
}
