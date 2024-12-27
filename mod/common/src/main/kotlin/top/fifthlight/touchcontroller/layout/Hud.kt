package top.fifthlight.touchcontroller.layout

import top.fifthlight.combine.paint.BlendFactor
import top.fifthlight.combine.paint.BlendFunction
import top.fifthlight.combine.paint.withBlend
import top.fifthlight.combine.paint.withBlendFunction
import top.fifthlight.touchcontroller.config.LayoutLayer

fun Context.Hud(layers: List<LayoutLayer>) {
    this.transformDrawQueue(
        drawTransform = { draw ->
            withBlend {
                withBlendFunction(
                    BlendFunction(
                        srcFactor = BlendFactor.SRC_ALPHA,
                        dstFactor = BlendFactor.ONE_MINUS_SRC_ALPHA,
                        srcAlpha = BlendFactor.ONE,
                        dstAlpha = BlendFactor.ZERO,
                    )
                ) {
                    draw()
                }
            }
        }
    ) {
        for (layer in layers) {
            if (!layer.condition.check(condition)) {
                continue
            }
            for (widget in layer.widgets) {
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
        }

        if (!inGui) {
            Inventory()
        }
    }

    if (!inGui) {
        View()
        Crosshair()
        if (config.showPointers) {
            Pointers()
        }
    }
}