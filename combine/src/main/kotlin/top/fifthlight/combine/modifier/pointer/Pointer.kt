package top.fifthlight.combine.modifier.pointer

import top.fifthlight.combine.input.PointerEvent
import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.PointerInputModifierNode

fun Modifier.onPointerInput(receiver: Placeable.(PointerEvent) -> Boolean) =
    then(PointerInputReceiverModifierNode(receiver))

private class PointerInputReceiverModifierNode(
    private val receiver: Placeable.(PointerEvent) -> Boolean
) : Modifier.Node<PointerInputReceiverModifierNode>,
    PointerInputModifierNode {
    override fun onPointerEvent(event: PointerEvent, node: Placeable): Boolean = receiver.invoke(node, event)
}