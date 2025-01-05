package top.fifthlight.combine.modifier

import top.fifthlight.combine.input.pointer.PointerEvent
import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.node.LayoutNode

fun interface PointerInputModifierNode {
    fun onPointerEvent(
        event: PointerEvent,
        node: Placeable,
        layoutNode: LayoutNode,
        children: (PointerEvent) -> Boolean
    ): Boolean
}
