package top.fifthlight.combine.paint

import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.ItemStack
import top.fifthlight.combine.data.Text
import top.fifthlight.data.*

enum class BlendFactor {
    ONE,
    ZERO,
    SRC_COLOR,
    SRC_ALPHA,
    ONE_MINUS_SRC_ALPHA,
    ONE_MINUS_SRC_COLOR,
    DST_COLOR,
    DST_ALPHA,
    ONE_MINUS_DST_ALPHA,
    ONE_MINUS_DST_COLOR,
}

data class BlendFunction(
    val srcFactor: BlendFactor,
    val dstFactor: BlendFactor,
    val srcAlpha: BlendFactor,
    val dstAlpha: BlendFactor
)

inline fun Canvas.withBlend(crossinline block: () -> Unit) {
    enableBlend()
    try {
        block()
    } finally {
        disableBlend()
    }
}

inline fun Canvas.withBlendFunction(
    func: BlendFunction,
    crossinline block: () -> Unit
) {
    blendFunction(func)
    try {
        block()
    } finally {
        defaultBlendFunction()
    }
}

interface Canvas {
    fun pushState()
    fun popState()
    fun translate(x: Int, y: Int)
    fun translate(x: Float, y: Float)
    fun rotate(degrees: Float)
    fun fillRect(offset: IntOffset = IntOffset.ZERO, size: IntSize = IntSize.ZERO, color: Color)
    fun drawText(offset: IntOffset, width: Int, text: String, color: Color)
    fun drawTextWithShadow(offset: IntOffset, width: Int, text: String, color: Color)
    fun drawText(offset: IntOffset, width: Int, text: Text, color: Color)
    fun drawTextWithShadow(offset: IntOffset, width: Int, text: Text, color: Color)
    fun drawTexture(id: Identifier, dstRect: Rect, uvRect: Rect = Rect.ONE, tint: Color = Colors.WHITE)
    fun drawGuiTexture(sprite: Identifier, dstRect: IntRect)
    fun drawItemStack(offset: IntOffset, size: IntSize = IntSize(16), stack: ItemStack)
    fun enableBlend()
    fun disableBlend()
    fun blendFunction(func: BlendFunction)
    fun defaultBlendFunction()
    fun pushClip(absoluteArea: IntRect, relativeArea: IntRect)
    fun popClip()
}

fun Canvas.translate(offset: IntOffset) = translate(offset.x, offset.y)
fun Canvas.translate(offset: Offset) = translate(offset.x, offset.y)

inline fun Canvas.withTranslate(x: Int, y: Int, crossinline block: Canvas.() -> Unit) {
    translate(x, y)
    try {
        block()
    } finally {
        translate(-x, -y)
    }
}

inline fun Canvas.withTranslate(x: Float, y: Float, crossinline block: Canvas.() -> Unit) {
    translate(x, y)
    try {
        block()
    } finally {
        translate(-x, -y)
    }
}

inline fun Canvas.withTranslate(offset: IntOffset, crossinline block: Canvas.() -> Unit) {
    translate(offset)
    try {
        block()
    } finally {
        translate(-offset)
    }
}

inline fun Canvas.withTranslate(offset: Offset, crossinline block: Canvas.() -> Unit) {
    translate(offset)
    try {
        block()
    } finally {
        translate(-offset)
    }
}
