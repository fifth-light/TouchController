package top.fifthlight.touchcontroller.layout

import com.mojang.blaze3d.platform.GlStateManager
import top.fifthlight.touchcontroller.control.ControllerWidget
import top.fifthlight.touchcontroller.ext.withBlend
import top.fifthlight.touchcontroller.ext.withBlendFunction

fun Context.Hud(widgets: List<ControllerWidget>) {
    this.transformDrawQueue(
        drawTransform = { draw ->
            withBlend {
                withBlendFunction(
                    srcFactor = GlStateManager.SrcFactor.SRC_ALPHA,
                    dstFactor = GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
                    srcAlpha = GlStateManager.SrcFactor.ONE,
                    dstAlpha = GlStateManager.DstFactor.ZERO
                ) {
                    draw()
                }
            }
        }
    ) {
        widgets.forEach { widget ->
            withOpacity(widget.opacity) {
                withAlign(
                    align = widget.align,
                    offset = widget.offset,
                    size = widget.size()
                ) {
                    widget.layout(this)
                }
            }
        }

        if (client.currentScreen == null) {
            Inventory()
        }
    }

    if (client.currentScreen == null) {
        View()
        Crosshair()
        if (config.showPointers) {
            Pointers()
        }
    }
}