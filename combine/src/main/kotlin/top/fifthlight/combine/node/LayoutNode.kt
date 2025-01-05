package top.fifthlight.combine.node

import androidx.compose.runtime.CompositionLocalMap
import top.fifthlight.combine.input.focus.FocusStateListener
import top.fifthlight.combine.input.input.TextInputReceiver
import top.fifthlight.combine.input.key.KeyEvent
import top.fifthlight.combine.input.key.KeyEventReceiver
import top.fifthlight.combine.input.pointer.PointerEvent
import top.fifthlight.combine.input.pointer.PointerEventReceiver
import top.fifthlight.combine.input.pointer.PointerEventType
import top.fifthlight.combine.layout.*
import top.fifthlight.combine.modifier.*
import top.fifthlight.combine.paint.NodeRenderer
import top.fifthlight.combine.paint.RenderContext

private fun interface Renderable {
    fun render(context: RenderContext)
}

internal sealed class WrapperLayoutNode(
    val node: LayoutNode,
) : Measurable, Placeable, Renderable, PointerEventReceiver, FocusStateListener, TextInputReceiver, KeyEventReceiver {
    var parent: WrapperLayoutNode? = null

    val parentPlaceable: Placeable?
        get() = parent ?: node.parent?.initialWrapper

    fun coerceConstraintBounds(constraints: Constraints, parent: Placeable) = object : Placeable by this {
        override var width: Int = parent.width.coerceIn(constraints.minWidth..constraints.maxWidth)
        override var height: Int = parent.height.coerceIn(constraints.minHeight..constraints.maxHeight)
    }

    class Node(node: LayoutNode) : WrapperLayoutNode(node), FocusStateListener, TextInputReceiver, KeyEventReceiver {
        override val parentData: Any? = node.parentData

        override var width: Int = 0
        override var height: Int = 0
        override var x: Int = 0
        override var y: Int = 0
        override val absoluteX: Int
            get() = (parentPlaceable?.absoluteX ?: 0) + x
        override val absoluteY: Int
            get() = (parentPlaceable?.absoluteY ?: 0) + y

        override fun measure(constraints: Constraints): Placeable {
            val result = node.measurePolicy.measure(node.children, constraints)

            width = result.width
            height = result.height
            result.placer.placeChildren()

            return coerceConstraintBounds(constraints, this)
        }

        override fun placeAt(x: Int, y: Int) {
            this.x = x
            this.y = y
        }

        override fun render(context: RenderContext) {
            context.withState {
                context.canvas.translate(x, y)
                node.renderer.renderInContext(context, this)
                node.children.forEach { child ->
                    child.render(context)
                }
            }
        }

        var pressEventTarget: LayoutNode? = null
        var moveEventTarget: LayoutNode? = null
        override fun onPointerEvent(event: PointerEvent): Boolean {
            val pressTarget = pressEventTarget
            val moveTarget = moveEventTarget
            var haveMoveChildren = false
            fun process(): Boolean {
                if (pressTarget != null) {
                    haveMoveChildren = true
                    return pressTarget.onPointerEvent(event)
                }
                for (child in node.children.asReversed()) {
                    if (event.position !in child) {
                        continue
                    }
                    if (event.type == PointerEventType.Move && !haveMoveChildren) {
                        if (moveTarget == null) {
                            moveEventTarget = child
                            child.onPointerEvent(event.copy(type = PointerEventType.Enter))
                        } else if (moveTarget != child) {
                            moveTarget.onPointerEvent(event.copy(type = PointerEventType.Leave))
                            child.onPointerEvent(event.copy(type = PointerEventType.Enter))
                            moveEventTarget = child
                        }
                        haveMoveChildren = true
                    }
                    if (child.onPointerEvent(event)) {
                        if (event.type == PointerEventType.Press) {
                            pressEventTarget = child
                        }
                        return true
                    }
                }
                return false
            }

            val result = process()
            if (event.type == PointerEventType.Release || event.type == PointerEventType.Cancel) {
                pressEventTarget = null
            }
            if (pressTarget == null) {
                if (event.type == PointerEventType.Move && !haveMoveChildren && moveTarget != null) {
                    moveTarget.onPointerEvent(event.copy(type = PointerEventType.Leave))
                    moveEventTarget = null
                }
                if (event.type == PointerEventType.Leave && moveTarget != null) {
                    moveTarget.onPointerEvent(event.copy(type = PointerEventType.Leave))
                    moveEventTarget = null
                }
            } else if (pressTarget == moveTarget && event.position !in pressTarget) {
                pressTarget.onPointerEvent(event.copy(type = PointerEventType.Leave))
                moveEventTarget = null
            }
            return result
        }

        override fun onFocusStateChanged(focused: Boolean) {}
        override fun onTextInput(string: String) {}
        override fun onKeyEvent(event: KeyEvent) {}
    }

    class Layout(
        node: LayoutNode,
        val children: WrapperLayoutNode,
        val modifierNode: LayoutModifierNode
    ) : WrapperLayoutNode(node),
        PointerEventReceiver by children,
        FocusStateListener by children,
        TextInputReceiver by children,
        KeyEventReceiver by children {
        override val parentData: Any? = children.parentData

        override var width: Int = 0
        override var height: Int = 0
        override var x: Int = 0
        override var y: Int = 0
        override val absoluteX: Int
            get() = (parentPlaceable?.absoluteX ?: 0) + x
        override val absoluteY: Int
            get() = (parentPlaceable?.absoluteY ?: 0) + y

        override fun placeAt(x: Int, y: Int) {
            this.x = x
            this.y = y
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
                context.canvas.translate(x, y)
                children.render(context)
            }
        }
    }

    abstract class PositionWrapper(
        node: LayoutNode,
        val children: WrapperLayoutNode,
    ) : WrapperLayoutNode(node),
        Measurable, Placeable, Renderable {
        override val parentData: Any? = children.parentData

        override val width: Int
            get() = children.width
        override val height: Int
            get() = children.height
        override var x: Int = 0
        override var y: Int = 0
        override val absoluteX: Int
            get() = (parentPlaceable?.absoluteX ?: 0) + x
        override val absoluteY: Int
            get() = (parentPlaceable?.absoluteY ?: 0) + y

        override fun placeAt(x: Int, y: Int) {
            this.x = x
            this.y = y
        }

        override fun measure(constraints: Constraints): Placeable {
            children.measure(constraints).placeAt(0, 0)
            return coerceConstraintBounds(constraints, this)
        }

        override fun render(context: RenderContext) {
            context.withState {
                context.canvas.translate(x, y)
                children.render(context)
            }
        }
    }

    class Draw(
        node: LayoutNode,
        children: WrapperLayoutNode,
        val modifierNode: DrawModifierNode
    ) : PositionWrapper(node, children),
        PointerEventReceiver by children,
        FocusStateListener by children,
        TextInputReceiver by children,
        KeyEventReceiver by children {

        override fun render(context: RenderContext) {
            context.withState {
                context.canvas.translate(x, y)
                modifierNode.renderBeforeContext(context, this)
                children.render(context)
                modifierNode.renderAfterContext(context, this)
            }
        }
    }

    class OnPlaced(
        node: LayoutNode,
        children: WrapperLayoutNode,
        private val modifierNode: PlaceListeningModifierNode,
    ) : PositionWrapper(node, children),
        PointerEventReceiver by children,
        FocusStateListener by children,
        TextInputReceiver by children,
        KeyEventReceiver by children {

        override fun measure(constraints: Constraints): Placeable {
            val result = super.measure(constraints)
            return object : Placeable by result {
                override fun placeAt(x: Int, y: Int) {
                    result.placeAt(x, y)
                    modifierNode.onPlaced(this)
                }
            }
        }
    }

    class PointerInput(
        node: LayoutNode,
        children: WrapperLayoutNode,
        private val modifierNode: PointerInputModifierNode,
    ) : PositionWrapper(node, children),
        FocusStateListener by children,
        TextInputReceiver by children,
        KeyEventReceiver by children {

        override fun onPointerEvent(event: PointerEvent): Boolean {
            val accepted = modifierNode.onPointerEvent(event, this, node) {
                children.onPointerEvent(it)
            }
            return if (accepted) {
                node.initialWrapper.pressEventTarget = null
                true
            } else {
                children.onPointerEvent(event)
            }
        }
    }

    class FocusState(
        node: LayoutNode,
        children: WrapperLayoutNode,
        private val modifierNode: FocusStateListenerModifierNode,
    ) : PositionWrapper(node, children),
        PointerEventReceiver by children,
        TextInputReceiver by children,
        KeyEventReceiver by children {

        override fun onFocusStateChanged(focused: Boolean) {
            modifierNode.onFocusStateChanged(focused)
            children.onFocusStateChanged(focused)
        }
    }

    class TextInput(
        node: LayoutNode,
        children: WrapperLayoutNode,
        private val modifierNode: TextInputModifierNode,
    ) : PositionWrapper(node, children),
        PointerEventReceiver by children,
        FocusStateListener by children,
        KeyEventReceiver by children {

        override fun onTextInput(string: String) = modifierNode.onTextInput(string)
    }

    class KeyInput(
        node: LayoutNode,
        children: WrapperLayoutNode,
        private val modifierNode: KeyInputModifierNode,
    ) : PositionWrapper(node, children),
        PointerEventReceiver by children,
        FocusStateListener by children,
        TextInputReceiver by children {

        override fun onKeyEvent(event: KeyEvent) = modifierNode.onKeyEvent(event)
    }
}

class LayoutNode : Measurable, Placeable, Renderable, PointerEventReceiver,
    FocusStateListener, TextInputReceiver, KeyEventReceiver {
    var parent: LayoutNode? = null
    val children = mutableListOf<LayoutNode>()
    var measurePolicy: MeasurePolicy = DefaultMeasurePolicy
    var renderer: NodeRenderer = NodeRenderer.EmptyRenderer
    var focusable: Boolean = false
    var compositionLocalMap: CompositionLocalMap = CompositionLocalMap.Empty
    override var parentData: Any? = null
    internal val initialWrapper = WrapperLayoutNode.Node(this)
    private var wrappedNode: WrapperLayoutNode = initialWrapper
    var modifier: Modifier = Modifier
        set(value) {
            field = value
            parentData = null
            focusable = false
            wrappedNode = buildWrapperLayoutNode(value)
        }

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
            if (node is PointerInputModifierNode) {
                val newWrapper = WrapperLayoutNode.PointerInput(this, currentWrapper, node)
                currentWrapper.parent = newWrapper
                currentWrapper = newWrapper
            }
            if (node is FocusStateListenerModifierNode) {
                node.onAttachedToNode(this)
                val newWrapper = WrapperLayoutNode.FocusState(this, currentWrapper, node)
                currentWrapper.parent = newWrapper
                currentWrapper = newWrapper
                focusable = true
            }
            if (node is TextInputModifierNode) {
                val newWrapper = WrapperLayoutNode.TextInput(this, currentWrapper, node)
                currentWrapper.parent = newWrapper
                currentWrapper = newWrapper
            }
            if (node is KeyInputModifierNode) {
                val newWrapper = WrapperLayoutNode.KeyInput(this, currentWrapper, node)
                currentWrapper.parent = newWrapper
                currentWrapper = newWrapper
            }
            if (node is ParentDataModifierNode) {
                parentData = node.modifierParentData(parentData)
            }
            currentWrapper
        }

    override fun measure(constraints: Constraints) = wrappedNode.measure(constraints)

    override val width: Int
        get() = wrappedNode.width
    override val height: Int
        get() = wrappedNode.height
    override val x: Int
        get() = wrappedNode.x
    override val y: Int
        get() = wrappedNode.y
    override val absoluteX: Int
        get() = wrappedNode.absoluteX
    override val absoluteY: Int
        get() = wrappedNode.absoluteY

    override fun placeAt(x: Int, y: Int) = wrappedNode.placeAt(x, y)

    override fun render(context: RenderContext) = wrappedNode.render(context)

    override fun onPointerEvent(event: PointerEvent) = wrappedNode.onPointerEvent(event)

    override fun onFocusStateChanged(focused: Boolean) = wrappedNode.onFocusStateChanged(focused)

    override fun onTextInput(string: String) = wrappedNode.onTextInput(string)

    override fun onKeyEvent(event: KeyEvent) = wrappedNode.onKeyEvent(event)

    internal companion object {
        val DefaultMeasurePolicy = MeasurePolicy { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) }
            MeasureResult(placeables.maxOfOrNull { it.width } ?: 0, placeables.maxOfOrNull { it.height } ?: 0) {
                placeables.forEach { it.placeAt(0, 0) }
            }
        }
    }
}