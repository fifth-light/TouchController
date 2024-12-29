package top.fifthlight.combine.modifier.pointer

import androidx.compose.runtime.Composable
import top.fifthlight.combine.input.PointerEvent
import top.fifthlight.combine.input.PointerEventType
import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.PointerInputModifierNode

@Composable
fun Modifier.consumePress() = then(PressConsumerModifierNode)

private data object PressConsumerModifierNode : Modifier.Node<PressConsumerModifierNode>, PointerInputModifierNode {
    override fun onPointerEvent(event: PointerEvent, node: Placeable, children: (PointerEvent) -> Boolean): Boolean {
        if (event.type == PointerEventType.Press) {
            children(event)
            return true
        } else {
            return false
        }
    }
}
