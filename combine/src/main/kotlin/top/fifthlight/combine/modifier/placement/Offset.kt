package top.fifthlight.combine.modifier.placement

import top.fifthlight.combine.layout.Measurable
import top.fifthlight.combine.layout.MeasureResult
import top.fifthlight.combine.layout.MeasureScope
import top.fifthlight.combine.modifier.Constraints
import top.fifthlight.combine.modifier.LayoutModifierNode
import top.fifthlight.combine.modifier.Modifier

fun Modifier.offset(size: Int = 0): Modifier = offset(size, size)

fun Modifier.offset(x: Int = 0, y: Int = 0): Modifier = then(OffsetNode(x, y))

private data class OffsetNode(
    val x: Int = 0,
    val y: Int = 0,
) : LayoutModifierNode, Modifier.Node<OffsetNode> {
    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeAt(x, y)
        }
    }
}
