package top.fifthlight.touchcontroller.layout

import org.koin.core.component.get
import top.fifthlight.combine.paint.*
import top.fifthlight.data.Offset

data class CrosshairStatus(
    val position: Offset,
    val breakPercent: Float,
) {
    val positionX
        get() = position.x

    val positionY
        get() = position.y
}

fun Context.Crosshair() {
    val status = result.crosshairStatus ?: return
    val crosshairRenderer: CrosshairRenderer = get()

    drawQueue.enqueue { canvas ->
        canvas.withTranslate(status.position * windowScaledSize) {
            withBlend {
                withBlendFunction(
                    func = BlendFunction(
                        srcFactor = BlendFactor.ONE_MINUS_DST_COLOR,
                        dstFactor = BlendFactor.ONE_MINUS_SRC_COLOR,
                        srcAlpha = BlendFactor.ONE,
                        dstAlpha = BlendFactor.ZERO
                    )
                ) {
                    val config = config.crosshair
                    crosshairRenderer.renderOuter(canvas, config)
                    if (status.breakPercent > 0f) {
                        val progress = status.breakPercent * (1f - config.initialProgress) + config.initialProgress
                        crosshairRenderer.renderInner(canvas, config, progress)
                    }
                }
            }
        }
    }
}