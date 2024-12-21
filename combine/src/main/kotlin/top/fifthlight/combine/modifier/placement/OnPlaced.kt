package top.fifthlight.combine.modifier.placement

import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.PlaceListeningModifierNode

fun Modifier.onPlaced(onPlaced: (Placeable) -> Unit) = then(OnPlacedNode(onPlaced))

private data class OnPlacedNode(
    val callback: (Placeable) -> Unit
) : PlaceListeningModifierNode, Modifier.Node<OnPlacedNode> {
    override fun onPlaced(placeable: Placeable) {
        callback.invoke(placeable)
    }
}
