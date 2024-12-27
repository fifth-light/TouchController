package top.fifthlight.combine.widget.base

import androidx.compose.runtime.Composable
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.node.LocalTextMeasurer
import top.fifthlight.combine.paint.Color
import top.fifthlight.combine.paint.Colors
import top.fifthlight.data.IntOffset

@Composable
fun Text(
    text: Text,
    modifier: Modifier = Modifier,
    color: Color = Colors.WHITE,
    shadow: Boolean = false,
) {
    val textMeasurer = LocalTextMeasurer.current
    Layout(
        modifier = modifier,
        measurePolicy = { _, constraints ->
            val measureResult = if (constraints.maxWidth == Int.MAX_VALUE) {
                textMeasurer.measure(text)
            } else {
                textMeasurer.measure(text, constraints.maxWidth)
            }
            layout(measureResult.width, measureResult.height) {}
        },
        renderer = { node ->
            if (shadow) {
                canvas.drawTextWithShadow(IntOffset.ZERO, node.width, text, color)
            } else {
                canvas.drawText(IntOffset.ZERO, node.width, text, color)
            }
        }
    )
}

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Colors.WHITE,
    shadow: Boolean = false,
) {
    val textMeasurer = LocalTextMeasurer.current
    Layout(
        modifier = modifier,
        measurePolicy = { _, constraints ->
            val measureResult = if (constraints.maxWidth == Int.MAX_VALUE) {
                textMeasurer.measure(text)
            } else {
                textMeasurer.measure(text, constraints.maxWidth)
            }
            layout(measureResult.width, measureResult.height) {}
        },
        renderer = { node ->
            if (shadow) {
                canvas.drawTextWithShadow(IntOffset.ZERO, node.width, text, color)
            } else {
                canvas.drawText(IntOffset.ZERO, node.width, text, color)
            }
        }
    )
}
