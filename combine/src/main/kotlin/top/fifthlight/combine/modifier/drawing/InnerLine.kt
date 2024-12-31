package top.fifthlight.combine.modifier.drawing

import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.DrawModifierNode
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.paint.Color
import top.fifthlight.combine.paint.RenderContext
import top.fifthlight.data.IntOffset

fun Modifier.innerLine(color: Color) = then(InnerLineNode(color))

private data class InnerLineNode(
    val color: Color,
) : DrawModifierNode, Modifier.Node<InnerLineNode> {
    override fun renderAfterContext(context: RenderContext, node: Placeable) {
        context.canvas.drawRect(IntOffset.ZERO, node.size, color)
    }
}