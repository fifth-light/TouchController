package top.fifthlight.combine.modifier.focus

import top.fifthlight.combine.input.Interaction
import top.fifthlight.combine.input.MutableInteractionSource
import top.fifthlight.combine.input.focus.LocalFocusManager
import top.fifthlight.combine.input.pointer.PointerEvent
import top.fifthlight.combine.input.pointer.PointerEventType
import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.FocusStateListenerModifierNode
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.PointerInputModifierNode
import top.fifthlight.combine.node.LayoutNode

interface FocusRequester {
    fun requestFocus()
}

private class FocusRequesterImpl : FocusRequester {
    var currentNode: LayoutNode? = null

    override fun requestFocus() {
        val node = currentNode ?: return
        node.compositionLocalMap[LocalFocusManager].requestFocus(node)
    }
}

fun FocusRequester(): FocusRequester = FocusRequesterImpl()

sealed class FocusInteraction : Interaction {
    data object Blur : FocusInteraction()
    data object Focus : FocusInteraction()
}

fun Modifier.focusable(
    interactionSource: MutableInteractionSource? = null,
    focusRequester: FocusRequester? = null,
) = then(
    FocusableModifierNode(
        interactionSource = interactionSource,
        focusRequester = focusRequester,
    )
)

data class FocusableModifierNode(
    val interactionSource: MutableInteractionSource?,
    val focusRequester: FocusRequester?,
) : Modifier.Node<FocusableModifierNode>, FocusStateListenerModifierNode, PointerInputModifierNode {
    override fun onAttachedToNode(node: LayoutNode) {
        (focusRequester as? FocusRequesterImpl)?.apply {
            currentNode = node
        }
    }

    override fun onFocusStateChanged(focused: Boolean) {
        interactionSource?.tryEmit(if (focused) FocusInteraction.Focus else FocusInteraction.Blur)
    }

    override fun onPointerEvent(
        event: PointerEvent,
        node: Placeable,
        layoutNode: LayoutNode,
        children: (PointerEvent) -> Boolean
    ): Boolean {
        if (event.type == PointerEventType.Press) {
            layoutNode.compositionLocalMap[LocalFocusManager].requestFocus(layoutNode)
            children(event)
            return true
        }
        return false
    }
}