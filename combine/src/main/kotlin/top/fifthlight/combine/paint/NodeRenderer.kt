package top.fifthlight.combine.paint

import top.fifthlight.combine.layout.Placeable

data class RenderContext(val canvas: Canvas) {
    inline fun withState(crossinline block: () -> Unit) {
        canvas.pushState()
        block()
        canvas.popState()
    }
}

fun interface NodeRenderer {
    fun renderInContext(context: RenderContext, node: Placeable) {
        with(context) {
            render(node)
        }
    }

    fun RenderContext.render(node: Placeable)

    companion object EmptyRenderer : NodeRenderer {
        override fun RenderContext.render(node: Placeable) = Unit
    }
}