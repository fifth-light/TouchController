package top.fifthlight.combine.widget.base.layout

import androidx.compose.runtime.Composable
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.ParentDataModifierNode
import top.fifthlight.data.IntSize

interface BoxScope {
    fun Modifier.alignment(alignment: Alignment) = then(BoxWeightModifier(alignment))

    companion object : BoxScope
}

private data class BoxParentData(
    val alignment: Alignment
)

private data class BoxWeightModifier(
    val alignment: Alignment
) : ParentDataModifierNode, Modifier.Node<BoxWeightModifier> {
    override fun modifierParentData(parentData: Any?): BoxParentData {
        val data = parentData as? BoxParentData
        if (data != null) {
            return data
        }
        return BoxParentData(alignment)
    }
}

@Composable
fun Box(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopLeft,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Layout(
        modifier = modifier,
        measurePolicy = { measurables, constraints ->
            val childConstraint = constraints.copy(minWidth = 0, minHeight = 0)
            val placeables = measurables.map { it.measure(childConstraint) }

            val width = (placeables.maxOfOrNull { it.width } ?: 0).coerceIn(constraints.minWidth, constraints.maxWidth)
            val height = (placeables.maxOfOrNull { it.height } ?: 0).coerceIn(constraints.minHeight, constraints.maxHeight)

            layout(width, height) {
                placeables.forEachIndexed { index, placeable ->
                    val measurable = measurables[index]
                    val parentData = measurable.parentData as? BoxParentData
                    val placeableAlignment = parentData?.alignment ?: alignment
                    val position = placeableAlignment.align(
                        IntSize(placeable.width, placeable.height),
                        IntSize(width, height)
                    )
                    placeable.placeAt(position.x, position.y)
                }
            }
        },
        content = {
            BoxScope.content()
        }
    )
}
