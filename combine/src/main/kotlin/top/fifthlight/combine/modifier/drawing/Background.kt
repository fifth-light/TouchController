package top.fifthlight.combine.modifier.drawing

import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.DrawModifierNode
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.paint.Color
import top.fifthlight.combine.paint.RenderContext
import top.fifthlight.data.*

fun Modifier.background(color: Color) = then(ColorBackgroundNode(color))

private data class ColorBackgroundNode(
    val color: Color
) : DrawModifierNode, Modifier.Node<ColorBackgroundNode> {
    override fun RenderContext.renderBefore(node: Placeable) {
        canvas.fillRect(
            offset = IntOffset(0, 0),
            size = IntSize(node.width, node.height),
            color = color
        )
    }
}

fun Modifier.textureBackground(id: Identifier, textureUv: Rect = Rect.ONE) = then(TextureBackgroundNode(id, textureUv))

private data class TextureBackgroundNode(
    val id: Identifier,
    val uvRect: Rect = Rect.ONE
) : DrawModifierNode, Modifier.Node<TextureBackgroundNode> {
    override fun RenderContext.renderBefore(node: Placeable) {
        canvas.drawTexture(
            id = id,
            dstRect = Rect(offset = Offset.ZERO, size = node.size.toSize()),
            uvRect = uvRect
        )
    }
}

fun Modifier.guiTextureBackground(sprite: Identifier) = then(GuiTextureBackgroundNode(sprite))

private data class GuiTextureBackgroundNode(
    val sprite: Identifier,
) : DrawModifierNode, Modifier.Node<TextureBackgroundNode> {
    override fun RenderContext.renderBefore(node: Placeable) {
        canvas.drawGuiTexture(
            sprite = sprite,
            dstRect = IntRect(offset = IntOffset.ZERO, size = node.size),
        )
    }
}
