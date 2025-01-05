package top.fifthlight.combine.modifier.pointer

import androidx.compose.runtime.Composable
import top.fifthlight.combine.input.pointer.PointerEvent
import top.fifthlight.combine.input.pointer.PointerEventType
import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.PointerInputModifierNode
import top.fifthlight.combine.node.LayoutNode

@Composable
fun Modifier.consumePress(onPress: () -> Unit = {}) = then(PressConsumerModifierNode(onPress))

private data class PressConsumerModifierNode(
    val onPress: () -> Unit
) : Modifier.Node<PressConsumerModifierNode>, PointerInputModifierNode {
    override fun onPointerEvent(
        event: PointerEvent,
        node: Placeable,
        layoutNode: LayoutNode,
        children: (PointerEvent) -> Boolean
    ): Boolean {
        if (event.type == PointerEventType.Press) {
            if (!children(event)) {
                onPress()
            }
            return true
        } else {
            return false
        }
    }
}
