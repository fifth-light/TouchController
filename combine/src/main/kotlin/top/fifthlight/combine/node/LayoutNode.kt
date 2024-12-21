package top.fifthlight.combine.node

import top.fifthlight.combine.input.PointerEvent
import top.fifthlight.combine.input.PointerEventReceiver
import top.fifthlight.combine.layout.Measurable
import top.fifthlight.combine.layout.MeasurePolicy
import top.fifthlight.combine.layout.MeasureResult
import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.*
import top.fifthlight.combine.paint.RenderContext
import top.fifthlight.combine.paint.Renderer

private fun interface Renderable {
    fun render(context: RenderContext)
}

private sealed class WrapperLayoutNode(
    val node: LayoutNode,
) : Measurable, Placeable, Renderable, PointerEventReceiver {
    var parent: WrapperLayoutNode? = null
    val parentPlaceable: Placeable?
        get() = parent ?: node.parent

    fun coerceConstraintBounds(constraints: Constraints, parent: Placeable) = object : Placeable by this {
        override var width: Int = parent.width.coerceIn(constraints.minWidth..constraints.maxWidth)
        override var height: Int = parent.height.coerceIn(constraints.minHeight..constraints.maxHeight)
    }

    class Node(node: LayoutNode) : WrapperLayoutNode(node), Placeable by node {
        override val parentData: Any? = node.parentData

        override fun measure(constraints: Constraints): Placeable {
            with(node) {
                val result = measurePolicy.measure(children, constraints)

                width = result.width
                height = result.height
                result.placer.placeChildren()

                return coerceConstraintBounds(constraints, this)
            }
        }

        override fun render(context: RenderContext) {
            with(node) {
                context.withState {
                    context.canvas.transform(x, y)
                    renderer.renderInContext(context)
                    children.forEach { child ->
                        child.render(context)
                    }
                }
            }
        }

        override fun onPointerEvent(event: PointerEvent) {
            node.children.forEach { child ->
                child.onPointerEvent(event)
            }
        }
    }

    class Layout(
        node: LayoutNode,
        val children: WrapperLayoutNode,
        val modifierNode: LayoutModifierNode
    ) : WrapperLayoutNode(node),
        PointerEventReceiver by children {
        override val parentData: Any? = children.parentData

        override var width: Int = 0
        override var height: Int = 0
        override var x: Int = 0
        override var y: Int = 0
        override var absoluteX: Int = 0
        override var absoluteY: Int = 0

        override fun placeAt(x: Int, y: Int) {
            this.x = x
            this.y = y
            this.absoluteX = (parentPlaceable?.absoluteX ?: 0) + x
            this.absoluteY = (parentPlaceable?.absoluteY ?: 0) + y
        }

        override fun measure(constraints: Constraints): Placeable {
            // Clear minimum constraints, so they will not be passed to children layout
            val result = modifierNode.measure(children, constraints)

            width = result.width
            height = result.height
            result.placer.placeChildren()

            return coerceConstraintBounds(constraints, this)
        }

        override fun render(context: RenderContext) {
            context.withState {
                context.canvas.transform(x, y)
                children.render(context)
            }
        }
    }

    class Draw(
        node: LayoutNode,
        val children: WrapperLayoutNode,
        val modifierNode: DrawModifierNode
    ) : WrapperLayoutNode(node),
        Measurable by children,
        PointerEventReceiver by children {
        override val width: Int
            get() = children.width
        override val height: Int
            get() = children.height
        override var x: Int = 0
        override var y: Int = 0
        override var absoluteX: Int = 0
        override var absoluteY: Int = 0

        override fun placeAt(x: Int, y: Int) {
            this.x = x
            this.y = y
            this.absoluteX = (parentPlaceable?.absoluteX ?: 0) + x
            this.absoluteY = (parentPlaceable?.absoluteY ?: 0) + y
        }

        override fun measure(constraints: Constraints): Placeable {
            children.measure(constraints)
            return coerceConstraintBounds(constraints, this)
        }

        override fun render(context: RenderContext) {
            context.withState {
                context.canvas.transform(x, y)
                modifierNode.renderBeforeContext(context, this)
                children.render(context)
                modifierNode.renderAfterContext(context, this)
            }
        }
    }

    class OnPlaced(
        node: LayoutNode,
        val children: WrapperLayoutNode,
        val modifierNode: PlaceListeningModifierNode
    ) : WrapperLayoutNode(node),
        Measurable by children,
        PointerEventReceiver by children,
        Renderable by children,
        Placeable by children {
        override fun measure(constraints: Constraints): Placeable {
            val result = children.measure(constraints)
            return object : Placeable by result {
                override fun placeAt(x: Int, y: Int) {
                    result.placeAt(x, y)
                    modifierNode.onPlaced(children)
                }
            }
        }
    }
}

class LayoutNode : Measurable, Placeable, Renderable, PointerEventReceiver {
    var parent: LayoutNode? = null
    val children = mutableListOf<LayoutNode>()

    var measurePolicy: MeasurePolicy = DefaultMeasurePolicy
    var renderer: Renderer = Renderer.EmptyRenderer

    private fun buildWrapperLayoutNode(modifier: Modifier): WrapperLayoutNode =
        modifier.foldIn<WrapperLayoutNode>(initialWrapper) { wrapper, node ->
            var currentWrapper = wrapper
            if (node is PlaceListeningModifierNode) {
                val newWrapper = WrapperLayoutNode.OnPlaced(this, currentWrapper, node)
                currentWrapper.parent = newWrapper
                currentWrapper = newWrapper
            }
            if (node is LayoutModifierNode) {
                val newWrapper = WrapperLayoutNode.Layout(this, currentWrapper, node)
                currentWrapper.parent = newWrapper
                currentWrapper = newWrapper
            }
            if (node is DrawModifierNode) {
                val newWrapper = WrapperLayoutNode.Draw(this, currentWrapper, node)
                currentWrapper.parent = newWrapper
                currentWrapper = newWrapper
            }
            if (node is ParentDataModifierNode) {
                parentData = node.modifierParentData(parentData)
            }
            currentWrapper
        }

    override var parentData: Any? = null
    private val initialWrapper = WrapperLayoutNode.Node(this)
    private var wrappedNode: WrapperLayoutNode = initialWrapper
    var modifier: Modifier = Modifier
        set(value) {
            field = value
            parent = null
            wrappedNode = buildWrapperLayoutNode(value)
        }

    override fun measure(constraints: Constraints) = wrappedNode.measure(constraints)

    override var width: Int = 0
    override var height: Int = 0
    override var x: Int = 0
    override var y: Int = 0
    override var absoluteX: Int = 0
    override var absoluteY: Int = 0

    override fun placeAt(x: Int, y: Int) {
        this.x = x
        this.y = y
        this.absoluteX = (parent?.absoluteX ?: 0) + x
        this.absoluteY = (parent?.absoluteY ?: 0) + y
    }

    override fun render(context: RenderContext) = wrappedNode.render(context)

    override fun onPointerEvent(event: PointerEvent) = wrappedNode.onPointerEvent(event)

    internal companion object {
        val DefaultMeasurePolicy = MeasurePolicy { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) }
            MeasureResult(placeables.maxOfOrNull { it.width } ?: 0, placeables.maxOfOrNull { it.height } ?: 0) {
                placeables.forEach { it.placeAt(0, 0) }
            }
        }
    }
}