package top.fifthlight.combine.paint

import top.fifthlight.combine.data.Identifier
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntRect
import top.fifthlight.data.IntSize
import top.fifthlight.data.Rect

interface Canvas {
    fun pushState()
    fun popState()
    fun transform(x: Int, y: Int)
    fun rotate(degrees: Float)
    fun fillRect(offset: IntOffset = IntOffset.ZERO, size: IntSize = IntSize.ZERO, color: Color)
    fun drawText(offset: IntOffset, text: String, color: Color)
    fun drawTextWithShadow(offset: IntOffset, text: String, color: Color)
    fun drawTexture(id: Identifier, dstRect: Rect, uvRect: Rect)
    fun drawGuiTexture(sprite: Identifier, dstRect: IntRect)
}
