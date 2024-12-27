package top.fifthlight.combine.modifier.drawing

import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.DrawModifierNode
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.paint.RenderContext

fun Modifier.rotate(degrees: Float) = then(RotateModifierNode(degrees))

private data class RotateModifierNode(
    val degrees: Float
) : DrawModifierNode, Modifier.Node<RotateModifierNode> {
    override fun renderBeforeContext(context: RenderContext, node: Placeable) {
        context.canvas.pushState()
        context.canvas.translate(node.width / 2, node.height / 2)
        context.canvas.rotate(degrees)
        context.canvas.translate(-node.width / 2, -node.height / 2)
    }

    override fun renderAfterContext(context: RenderContext, node: Placeable) {
        context.canvas.popState()
    }
}
