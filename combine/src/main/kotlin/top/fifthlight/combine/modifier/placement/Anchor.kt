package top.fifthlight.combine.modifier.placement

import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.PlaceListeningModifierNode
import top.fifthlight.data.IntRect

fun Modifier.anchor(onAnchorUpdated: (IntRect) -> Unit) = then(AnchorNode(onAnchorUpdated))

private data class AnchorNode(
    val onAnchorUpdated: (IntRect) -> Unit
) : PlaceListeningModifierNode, Modifier.Node<AnchorNode> {
    override fun onPlaced(placeable: Placeable) {
        onAnchorUpdated(
            IntRect(
                offset = placeable.absolutePosition,
                size = placeable.size,
            )
        )
    }
}
