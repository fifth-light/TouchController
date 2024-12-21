package top.fifthlight.combine.modifier.placement

import top.fifthlight.combine.layout.Measurable
import top.fifthlight.combine.layout.MeasureResult
import top.fifthlight.combine.layout.MeasureScope
import top.fifthlight.combine.modifier.Constraints
import top.fifthlight.combine.modifier.LayoutModifierNode
import top.fifthlight.combine.modifier.Modifier

fun Modifier.fillMaxSize(fraction: Float = 1f) = then(FillNode(width = fraction, height = fraction))

fun Modifier.fillMaxWidth(fraction: Float = 1f) = then(FillNode(width = fraction))

fun Modifier.fillMaxHeight(fraction: Float = 1f) = then(FillNode(height = fraction))

private data class FillNode(
    val width: Float? = null,
    val height: Float? = null
) : LayoutModifierNode, Modifier.Node<FillNode> {
    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        fun transform(fraction: Float, size: Int): Int = if (size == Int.MAX_VALUE) {
            Int.MAX_VALUE
        } else {
            (size * fraction).toInt()
        }

        var targetConstraints = constraints
        if (width != null) {
            val width = transform(width, targetConstraints.maxWidth).coerceIn(targetConstraints.minWidth, targetConstraints.maxWidth)
            targetConstraints = targetConstraints.copy(
                minWidth = width,
                maxWidth = width,
            )
        }
        if (height != null) {
            val height = transform(height, targetConstraints.maxHeight).coerceIn(targetConstraints.minHeight, targetConstraints.maxHeight)
            targetConstraints = targetConstraints.copy(
                minHeight = height,
                maxHeight = height
            )
        }

        val placeable = measurable.measure(targetConstraints)

        return layout(placeable.width, placeable.height) {
            placeable.placeAt(0, 0)
        }
    }
}
