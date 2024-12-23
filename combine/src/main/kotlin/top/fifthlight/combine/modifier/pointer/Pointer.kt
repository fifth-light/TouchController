package top.fifthlight.combine.modifier.pointer

import top.fifthlight.combine.input.PointerEventReceiver
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.PointerInputModifierNode

fun Modifier.onPointerInput(receiver: PointerEventReceiver) = then(PointerInputReceiverModifierNode(receiver))

private class PointerInputReceiverModifierNode(receiver: PointerEventReceiver) :
    Modifier.Node<PointerInputReceiverModifierNode>,
    PointerInputModifierNode,
    PointerEventReceiver by receiver