package top.fifthlight.touchcontroller.layout

fun Context.Color(color: Int) = Color(color.toUInt())

fun Context.Color(color: UInt) {
    drawQueue.enqueue { drawContext, _ ->
        drawContext.fill(0, 0, size.width, size.height, color.toInt())
    }
}