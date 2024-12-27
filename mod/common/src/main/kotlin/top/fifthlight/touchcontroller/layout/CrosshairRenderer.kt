package top.fifthlight.touchcontroller.layout

import top.fifthlight.combine.paint.Canvas
import top.fifthlight.touchcontroller.config.CrosshairConfig

interface CrosshairRenderer {
    fun renderOuter(canvas: Canvas, config: CrosshairConfig)
    fun renderInner(canvas: Canvas, config: CrosshairConfig, progress: Float)
}