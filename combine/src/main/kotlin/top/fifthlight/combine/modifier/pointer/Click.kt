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

sealed class ClickInteraction : Interaction {
    data object Empty : ClickInteraction()
    data object Hover : ClickInteraction()
    data object Active : ClickInteraction()
}

class ClickState internal constructor(
    internal var pressed: Boolean = false,
    internal var entered: Boolean = false,
)

@Composable
fun Modifier.clickable(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    clickState: ClickState = remember { ClickState() },
    onClick: () -> Unit
) = then(ClickableModifierNode(interactionSource, clickState, onClick))

private data class ClickableModifierNode(
    val interactionSource: MutableInteractionSource,
    val clickState: ClickState,
    val onClick: () -> Unit,
) : Modifier.Node<ClickableModifierNode>, PointerInputModifierNode {

    override fun onPointerEvent(event: PointerEvent, node: Placeable): Boolean {
        when (event.type) {
            PointerEventType.Press -> clickState.pressed = true
            PointerEventType.Move -> {}
            PointerEventType.Enter -> clickState.entered = true
            PointerEventType.Leave -> clickState.entered = false
            PointerEventType.Cancel -> clickState.pressed = false
            PointerEventType.Release -> {
                if (clickState.pressed && clickState.entered) {
                    onClick()
                }
                clickState.pressed = false
            }
            else -> return false
        }
        if (clickState.pressed) {
            if (clickState.entered) {
                interactionSource.tryEmit(ClickInteraction.Active)
            } else {
                interactionSource.tryEmit(ClickInteraction.Empty)
            }
        } else {
            if (clickState.entered) {
                interactionSource.tryEmit(ClickInteraction.Hover)
            } else {
                interactionSource.tryEmit(ClickInteraction.Empty)
            }
        }
        return true
    }
}
