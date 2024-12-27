package top.fifthlight.touchcontroller.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import top.fifthlight.touchcontroller.event.HudRenderCallback.CrosshairRender

object HudRenderCallback {
    val CROSSHAIR: Event<CrosshairRender> = EventFactory.createArrayBacked(
        CrosshairRender::class.java
    ) { listeners: Array<CrosshairRender> ->
        CrosshairRender { drawContext, tickCounter ->
            var shouldRender = true
            for (event in listeners) {
                if (!event.onCrosshairRender(drawContext, tickCounter)) {
                    shouldRender = false
                }
            }
            shouldRender
        }
    }

    fun interface CrosshairRender {
        fun onCrosshairRender(drawContext: DrawContext, tickCounter: RenderTickCounter): Boolean
    }
}