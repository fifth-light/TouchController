package top.fifthlight.combine.modifier.pointer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import top.fifthlight.combine.input.Interaction
import top.fifthlight.combine.input.MutableInteractionSource
import top.fifthlight.combine.input.PointerEvent
import top.fifthlight.combine.input.PointerEventType
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.PointerInputModifierNode

sealed class HoverInteraction : Interaction {
    data object Empty : HoverInteraction()
    data object Hover : HoverInteraction()
}

@Composable
fun Modifier.hoverable(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onHovered: (Boolean) -> Unit
) = then(HoverableModifierNode(interactionSource, onHovered))

data class HoverableModifierNode(
    val interactionSource: MutableInteractionSource,
    val onHovered: (Boolean) -> Unit,
) : Modifier.Node<HoverableModifierNode>, PointerInputModifierNode {

    override fun onPointerEvent(event: PointerEvent): Boolean {
        when (event.type) {
            PointerEventType.Enter -> {
                onHovered(true)
                interactionSource.tryEmit(HoverInteraction.Hover)
            }
            PointerEventType.Leave -> {
                onHovered(false)
                interactionSource.tryEmit(HoverInteraction.Empty)
            }
        }
        return false
    }
}