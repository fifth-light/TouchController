package top.fifthlight.combine.modifier.drawing

import top.fifthlight.combine.data.Texture
import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.DrawModifierNode
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.paint.Color
import top.fifthlight.combine.paint.GuiTexture
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

fun Modifier.textureBackground(texture: Texture, textureUv: Rect = Rect.ONE) =
    then(TextureBackgroundNode(texture, textureUv))

private data class TextureBackgroundNode(
    val texture: Texture,
    val uvRect: Rect = Rect.ONE
) : DrawModifierNode, Modifier.Node<TextureBackgroundNode> {
    override fun RenderContext.renderBefore(node: Placeable) {
        canvas.drawTexture(
            texture = texture,
            dstRect = Rect(offset = Offset.ZERO, size = node.size.toSize()),
            uvRect = uvRect
        )
    }
}

fun Modifier.guiTextureBackground(texture: GuiTexture) = then(GuiTextureBackgroundNode(texture))

private data class GuiTextureBackgroundNode(
    val texture: GuiTexture,
) : DrawModifierNode, Modifier.Node<TextureBackgroundNode> {
    override fun RenderContext.renderBefore(node: Placeable) {
        canvas.drawGuiTexture(
            texture = texture,
            dstRect = IntRect(offset = IntOffset.ZERO, size = node.size),
        )
    }
}
