package top.fifthlight.combine.paint

data class RenderContext(val canvas: Canvas) {
    inline fun withState(crossinline block: () -> Unit) {
        canvas.pushState()
        block()
        canvas.popState()
    }
}

fun interface Renderer {
    fun renderInContext(context: RenderContext) {
        with(context) {
            render()
        }
    }

    fun RenderContext.render()

    companion object EmptyRenderer : Renderer {
        override fun RenderContext.render() = Unit
    }
}