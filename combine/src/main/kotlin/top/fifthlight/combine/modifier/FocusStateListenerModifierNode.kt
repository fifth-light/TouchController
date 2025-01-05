package top.fifthlight.combine.modifier

import top.fifthlight.combine.input.focus.FocusStateListener
import top.fifthlight.combine.node.LayoutNode

interface FocusStateListenerModifierNode : FocusStateListener {
    fun onAttachedToNode(node: LayoutNode) = Unit
}
