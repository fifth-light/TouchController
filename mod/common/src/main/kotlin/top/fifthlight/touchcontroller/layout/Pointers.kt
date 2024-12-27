package top.fifthlight.touchcontroller.layout

import top.fifthlight.combine.paint.withTranslate

fun Context.Pointers() {
    drawQueue.enqueue { canvas ->
        pointers.forEach { (id, pointer) ->
            canvas.withTranslate(pointer.scaledOffset) {
                /*fillRect(-1, -1, 1, 1, Colors.WHITE)
                drawContext.drawBorder(-4, -4, 8, 8, Colors.WHITE)
                val text = "$id"
                drawText(textRenderer, text, -textRenderer.getWidth(text) / 2, 8, Colors.WHITE, false)*/
                TODO()
            }
        }
    }
}