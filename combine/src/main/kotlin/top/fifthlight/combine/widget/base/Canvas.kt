package top.fifthlight.combine.widget.base

import androidx.compose.runtime.Composable
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.paint.NodeRenderer

@Composable
fun Canvas(
    modifier: Modifier = Modifier,
    renderer: NodeRenderer,
) {
    Layout(
        modifier = modifier,
        measurePolicy = { _, constraints ->
            layout(
                width = constraints.minWidth,
                height = constraints.minHeight
            ) {
            }
        },
        renderer = renderer,
        content = {}
    )
}
