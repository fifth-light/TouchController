package top.fifthlight.combine.modifier.placement

import top.fifthlight.combine.layout.Measurable
import top.fifthlight.combine.layout.MeasureResult
import top.fifthlight.combine.layout.MeasureScope
import top.fifthlight.combine.modifier.Constraints
import top.fifthlight.combine.modifier.LayoutModifierNode
import top.fifthlight.combine.modifier.Modifier

fun Modifier.minSize(width: Int, height: Int): Modifier = then(MinSizeNode(width = width, height = height))

fun Modifier.minWidth(width: Int): Modifier = then(MinSizeNode(width = width))

fun Modifier.minHeight(height: Int): Modifier = then(MinSizeNode(height = height))

private data class MinSizeNode(
    val width: Int? = null,
    val height: Int? = null
) : LayoutModifierNode, Modifier.Node<MinSizeNode> {
    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints.copy(
            minWidth = width?.coerceAtLeast(constraints.minWidth) ?: constraints.minWidth,
            minHeight = height?.coerceAtLeast(constraints.minHeight) ?: constraints.minHeight,
        ))

        return layout(placeable.width, placeable.height) {
            placeable.placeAt(0, 0)
        }
    }
}
