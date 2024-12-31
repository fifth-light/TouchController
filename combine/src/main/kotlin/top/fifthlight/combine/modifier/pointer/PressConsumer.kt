package top.fifthlight.combine.modifier.pointer

import androidx.compose.runtime.Composable
import top.fifthlight.combine.input.PointerEvent
import top.fifthlight.combine.input.PointerEventType
import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.PointerInputModifierNode

@Composable
fun Modifier.consumePress(onPress: () -> Unit = {}) = then(PressConsumerModifierNode(onPress))

private data class PressConsumerModifierNode(
    val onPress: () -> Unit
) : Modifier.Node<PressConsumerModifierNode>, PointerInputModifierNode {
    override fun onPointerEvent(event: PointerEvent, node: Placeable, children: (PointerEvent) -> Boolean): Boolean {
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
