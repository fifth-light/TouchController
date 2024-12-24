package top.fifthlight.combine.widget.base

import androidx.compose.runtime.Composable
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.size
import top.fifthlight.combine.paint.NodeRenderer

@Composable
fun Canvas(
    modifier: Modifier = Modifier,
    width: Int,
    height: Int,
    renderer: NodeRenderer,
) {
    Layout(
        modifier = modifier.size(width, height),
        measurePolicy = { _, constraints ->
            layout(
                width = width.coerceIn(constraints.minWidth, constraints.maxWidth),
                height = height.coerceIn(constraints.minHeight, constraints.maxHeight)
            ) {
            }
        },
        renderer = renderer,
        content = {}
    )
}
