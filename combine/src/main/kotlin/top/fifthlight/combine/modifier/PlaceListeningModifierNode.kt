package top.fifthlight.combine.modifier

import top.fifthlight.combine.layout.Placeable

interface PlaceListeningModifierNode {
    fun onPlaced(placeable: Placeable)
}