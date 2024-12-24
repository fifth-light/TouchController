package top.fifthlight.combine.widget.base.layout

import androidx.compose.runtime.Composable
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.ParentDataModifierNode

interface ColumnScope {
    fun Modifier.weight(weight: Float) = then(ColumnWeightModifier(weight))

    companion object : ColumnScope
}

private data class ColumnParentData(
    val weight: Float
)

private data class ColumnWeightModifier(
    val weight: Float
) : ParentDataModifierNode, Modifier.Node<ColumnWeightModifier> {
    override fun modifierParentData(parentData: Any?): ColumnParentData {
        val data = parentData as? ColumnParentData
        if (data != null) {
            return data
        }
        return ColumnParentData(weight)
    }
}

@Composable
fun Column(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Left,
    content: @Composable ColumnScope.() -> Unit
) {
    Layout(
        content = {
            ColumnScope.content()
        },
        modifier = modifier,
        measurePolicy = { measurables, constraints ->
            val allSpacing = verticalArrangement.spacing * (measurables.size - 1)
            val childConstraint = constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxHeight = constraints.maxHeight - allSpacing
            )

            val heights = IntArray(measurables.size)
            val yPositions = IntArray(measurables.size)
            var allHeight = 0
            var maxWidth = 0
            var totalWeight = 0f

            val placeables = Array<Placeable?>(measurables.size) { null }
            measurables.forEachIndexed { index, measurable ->
                val parentData = measurable.parentData as? ColumnParentData
                if (parentData != null) {
                    heights[index] = -1
                    totalWeight += parentData.weight
                } else {
                    val placeable = measurable.measure(childConstraint)
                    heights[index] = placeable.height
                    allHeight += placeable.height
                    maxWidth = maxOf(maxWidth, placeable.width)
                    placeables[index] = placeable
                }
            }

            val weightUnitSpace = if (totalWeight > 0f) {
                val allSpace = constraints.maxHeight - allHeight - allSpacing
                val unitSpace = allSpace / totalWeight
                allHeight += constraints.maxHeight
                unitSpace
            } else {
                0f
            }

            for (i in heights.indices) {
                if (heights[i] == -1) {
                    val weight = (measurables[i].parentData as ColumnParentData).weight
                    heights[i] = (weightUnitSpace * weight).toInt()
                    val placeable = measurables[i].measure(
                        constraints = childConstraint.copy(
                            minHeight = heights[i],
                            maxHeight = heights[i],
                        )
                    )
                    maxWidth = maxOf(maxWidth, placeable.width)
                    placeables[i] = placeable
                }
            }

            val width = maxWidth.coerceIn(constraints.minWidth, constraints.maxWidth)
            val height = (allHeight + allSpacing).coerceIn(constraints.minHeight, constraints.maxHeight)

            verticalArrangement.arrangeVertically(
                totalSize = height,
                sizes = heights,
                outPositions = yPositions
            )

            layout(width, height) {
                placeables.forEachIndexed { index, placeable ->
                    placeable!!.placeAt(horizontalAlignment.align(placeable.width, width), yPositions[index])
                }
            }
        }
    )
}
