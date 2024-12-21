package top.fifthlight.combine.widget.base.layout

import androidx.compose.runtime.Composable
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.data.IntSize

@Composable
fun Box(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopLeft,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        measurePolicy = { measurables, constraints ->
            val childConstraint = constraints.copy(minWidth = 0, minHeight = 0)
            val placeables = measurables.map { it.measure(childConstraint) }

            val width = (placeables.maxOfOrNull { it.width } ?: 0).coerceIn(constraints.minWidth, constraints.maxWidth)
            val height = (placeables.maxOfOrNull { it.height } ?: 0).coerceIn(constraints.minHeight, constraints.maxHeight)

            layout(width, height) {
                placeables.forEach { placeable ->
                    val position = alignment.align(
                        IntSize(placeable.width, placeable.height),
                        IntSize(width, height)
                    )
                    placeable.placeAt(position.x, position.y)
                }
            }
        },
        content = content
    )
}
