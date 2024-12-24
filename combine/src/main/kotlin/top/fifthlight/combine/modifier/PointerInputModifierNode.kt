package top.fifthlight.combine.modifier

import top.fifthlight.combine.input.PointerEvent
import top.fifthlight.combine.layout.Placeable

fun interface PointerInputModifierNode {
    fun onPointerEvent(event: PointerEvent, node: Placeable): Boolean
}
