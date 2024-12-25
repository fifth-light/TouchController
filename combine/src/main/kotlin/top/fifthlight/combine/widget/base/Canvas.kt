package top.fifthlight.combine.widget.base

import androidx.compose.runtime.Composable
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.layout.MeasurePolicy
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.paint.NodeRenderer

private val canvasDefaultMeasurePolicy = MeasurePolicy { _, constraints ->
    layout(
        width = constraints.minWidth,
        height = constraints.minHeight
    ) {
    }
}

@Composable
fun Canvas(
    modifier: Modifier = Modifier,
    measurePolicy: MeasurePolicy = canvasDefaultMeasurePolicy,
    renderer: NodeRenderer,
) {
    Layout(
        modifier = modifier,
        measurePolicy = measurePolicy,
        renderer = renderer,
        content = {}
    )
}
