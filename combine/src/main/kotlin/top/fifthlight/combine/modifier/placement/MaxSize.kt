package top.fifthlight.combine.modifier.placement

import top.fifthlight.combine.layout.Measurable
import top.fifthlight.combine.layout.MeasureResult
import top.fifthlight.combine.layout.MeasureScope
import top.fifthlight.combine.modifier.Constraints
import top.fifthlight.combine.modifier.LayoutModifierNode
import top.fifthlight.combine.modifier.Modifier

fun Modifier.maxSize(width: Int, height: Int): Modifier = then(MaxSizeNode(width = width, height = height))

fun Modifier.maxWidth(width: Int): Modifier = then(MaxSizeNode(width = width))

fun Modifier.maxHeight(height: Int): Modifier = then(MaxSizeNode(height = height))

private data class MaxSizeNode(
    val width: Int? = null,
    val height: Int? = null
) : LayoutModifierNode, Modifier.Node<MaxSizeNode> {
    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(
            constraints.copy(
                maxWidth = width?.coerceAtMost(constraints.maxWidth) ?: constraints.maxWidth,
                maxHeight = height?.coerceAtMost(constraints.maxHeight) ?: constraints.maxHeight,
            )
        )

        return layout(placeable.width, placeable.height) {
            placeable.placeAt(0, 0)
        }
    }
}
