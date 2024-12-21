package top.fifthlight.combine.modifier

import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.paint.RenderContext

interface DrawModifierNode {
    fun renderBeforeContext(context: RenderContext, node: Placeable) {
        with(context) {
            renderBefore(node)
        }
    }

    fun renderAfterContext(context: RenderContext, node: Placeable) {
        with(context) {
            renderAfter(node)
        }
    }

    fun RenderContext.renderBefore(node: Placeable) {}
    fun RenderContext.renderAfter(node: Placeable) {}
}