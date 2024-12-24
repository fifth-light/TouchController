package top.fifthlight.combine.widget.base.layout

import androidx.compose.runtime.Composable
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.ParentDataModifierNode

interface RowScope {
    fun Modifier.weight(weight: Float) = then(RowWeightModifier(weight))

    companion object : RowScope
}

private data class RowWeightParentData(
    val weight: Float,
)

private data class RowWeightModifier(
    val weight: Float,
) : ParentDataModifierNode, Modifier.Node<RowWeightModifier> {
    override fun modifierParentData(parentData: Any?): RowWeightParentData {
        val data = parentData as? RowWeightParentData
        if (data != null) {
            return data
        }
        return RowWeightParentData(weight)
    }
}

@Composable
fun Row(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Left,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit
) {
    Layout(
        content = {
            RowScope.content()
        },
        modifier = modifier,
        measurePolicy = { measurables, constraints ->
            val allSpacing = horizontalArrangement.spacing * (measurables.size - 1)
            val childConstraint = constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxWidth = constraints.maxWidth - allSpacing
            )

            val widths = IntArray(measurables.size)
            val xPositions = IntArray(measurables.size)
            var allWidth = 0
            var maxHeight = 0
            var totalWeight = 0f

            val placeables = Array<Placeable?>(measurables.size) { null }
            measurables.forEachIndexed { index, measurable ->
                val parentData = measurable.parentData as? RowWeightParentData
                if (parentData != null) {
                    widths[index] = -1
                    totalWeight += parentData.weight
                } else {
                    val placeable = measurable.measure(childConstraint)
                    widths[index] = placeable.width
                    allWidth += placeable.width
                    maxHeight = maxOf(maxHeight, placeable.height)
                    placeables[index] = placeable
                }
            }

            val weightUnitSpace = if (totalWeight > 0f) {
                val allSpace = constraints.maxWidth - allWidth - allSpacing
                val unitSpace = allSpace / totalWeight
                allWidth += constraints.maxWidth
                unitSpace
            } else {
                0f
            }

            for (i in widths.indices) {
                if (widths[i] == -1) {
                    val weight = (measurables[i].parentData as RowWeightParentData).weight
                    widths[i] = (weightUnitSpace * weight).toInt()
                    val placeable = measurables[i].measure(
                        constraints = childConstraint.copy(
                            minWidth = widths[i],
                            maxWidth = widths[i],
                        )
                    )
                    maxHeight = maxOf(maxHeight, placeable.height)
                    placeables[i] = placeable
                }
            }

            val width = (allWidth + allSpacing).coerceIn(constraints.minWidth, constraints.maxWidth)
            val height = maxHeight.coerceIn(constraints.minHeight, constraints.maxHeight)

            horizontalArrangement.arrangeHorizontally(
                totalSize = width,
                sizes = widths,
                outPositions = xPositions
            )

            layout(width, height) {
                placeables.forEachIndexed { index, placeable ->
                    placeable!!.placeAt(xPositions[index], verticalAlignment.align(placeable.height, height))
                }
            }
        }
    )
}
