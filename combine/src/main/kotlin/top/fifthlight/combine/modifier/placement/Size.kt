package top.fifthlight.combine.modifier.placement

import top.fifthlight.combine.layout.Measurable
import top.fifthlight.combine.layout.MeasureResult
import top.fifthlight.combine.layout.MeasureScope
import top.fifthlight.combine.modifier.Constraints
import top.fifthlight.combine.modifier.LayoutModifierNode
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.data.IntSize

fun Modifier.size(size: Int) = size(size, size)

fun Modifier.size(size: IntSize) = size(size.width, size.height)

fun Modifier.size(width: Int, height: Int) = then(SizeNode(width = width, height = height))

fun Modifier.width(width: Int) = then(SizeNode(width = width))

fun Modifier.height(height: Int) = then(SizeNode(height = height))

private data class SizeNode(
    val width: Int? = null,
    val height: Int? = null
) : LayoutModifierNode, Modifier.Node<SizeNode> {
    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(
            constraints.copy(
                minWidth = width ?: constraints.minWidth,
                minHeight = height ?: constraints.minHeight,
                maxWidth = width ?: constraints.maxWidth,
                maxHeight = height ?: constraints.maxHeight,
            )
        )

        return layout(placeable.width, placeable.height) {
            placeable.placeAt(0, 0)
        }
    }
}
