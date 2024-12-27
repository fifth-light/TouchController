package top.fifthlight.touchcontroller.layout

import top.fifthlight.combine.paint.Color
import top.fifthlight.data.IntOffset

fun Context.Color(color: Color) {
    drawQueue.enqueue { canvas ->
        canvas.fillRect(IntOffset.ZERO, size, color)
    }
}