package top.fifthlight.combine.widget.base.layout

import androidx.compose.runtime.Composable
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.data.IntOffset
import kotlin.math.max

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    // TODO add arrangement support for flow line
    Layout(
        modifier = modifier,
        measurePolicy = { measurables, constraints ->
            val childConstraint = constraints.copy(minWidth = 0, minHeight = 0)

            val childPositions = Array(measurables.size) { IntOffset.ZERO }
            var cursorPosition = IntOffset(0, 0)
            var maxWidth = 0
            var rowMaxHeight = 0

            val placeables = measurables.mapIndexed { index, measurable ->
                val placeable = measurable.measure(childConstraint)
                if (placeable.width + cursorPosition.left > constraints.maxWidth) {
                    // Break line
                    cursorPosition = IntOffset(0, cursorPosition.y + rowMaxHeight)
                    rowMaxHeight = 0
                }
                childPositions[index] = cursorPosition
                cursorPosition = IntOffset(cursorPosition.x + placeable.width, cursorPosition.y)
                maxWidth = max(maxWidth, cursorPosition.x)
                rowMaxHeight = max(rowMaxHeight, placeable.height)
                placeable
            }

            val width = maxWidth.coerceIn(constraints.minWidth, constraints.maxWidth)
            val height = (cursorPosition.y + rowMaxHeight).coerceIn(constraints.minHeight, constraints.maxHeight)

            layout(width, height) {
                placeables.forEachIndexed { index, placeable ->
                    placeable.placeAt(childPositions[index])
                }
            }
        },
        content = {
            content()
        }
    )
}