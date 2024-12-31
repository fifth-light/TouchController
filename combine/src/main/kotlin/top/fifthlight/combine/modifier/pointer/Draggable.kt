package top.fifthlight.combine.modifier.pointer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import top.fifthlight.combine.input.Interaction
import top.fifthlight.combine.input.MutableInteractionSource
import top.fifthlight.combine.input.PointerEvent
import top.fifthlight.combine.input.PointerEventType
import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.PointerInputModifierNode
import top.fifthlight.data.Offset

sealed class DragInteraction : Interaction {
    data object Empty : ClickInteraction()
    data object Active : ClickInteraction()
}

class DragState internal constructor(
    internal var pressed: Boolean = false,
    internal var lastPosition: Offset? = null
)

@Composable
fun Modifier.draggable(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    dragState: DragState = remember { DragState() },
    onDrag: (Offset) -> Unit
) = then(DraggableModifierNode(interactionSource, dragState, onDrag = onDrag))

private data class DraggableModifierNode(
    val interactionSource: MutableInteractionSource,
    val dragState: DragState,
    val onDrag: (Offset) -> Unit,
) : Modifier.Node<DraggableModifierNode>, PointerInputModifierNode {

    override fun onPointerEvent(event: PointerEvent, node: Placeable, children: (PointerEvent) -> Boolean): Boolean {
        when (event.type) {
            PointerEventType.Press -> {
                dragState.pressed = true
                dragState.lastPosition = event.position
            }

            PointerEventType.Move -> {
                if (dragState.pressed) {
                    val lastPosition = dragState.lastPosition
                    if (lastPosition != null) {
                        val diff = event.position - lastPosition
                        onDrag(diff)
                    } else {
                        onDrag(Offset.ZERO)
                    }
                    dragState.lastPosition = event.position
                }
            }

            PointerEventType.Release -> dragState.pressed = false

            else -> return false
        }
        if (dragState.pressed) {
            interactionSource.tryEmit(DragInteraction.Active)
        } else {
            interactionSource.tryEmit(DragInteraction.Empty)
        }
        return true
    }
}
