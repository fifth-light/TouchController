package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.Composable
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.minHeight
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.widget.base.Canvas
import top.fifthlight.data.IntOffset

@Composable
fun EditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    placeholder: Text? = null,
) {
    Canvas(
        modifier = Modifier
            .minHeight(20)
            /* .keyInput() */
            .then(modifier),
    ) { node ->
        with(canvas) {
            fillRect(offset = IntOffset.ZERO, size = node.size, color = Colors.WHITE)
            fillRect(offset = IntOffset(1, 1), size = node.size - 2, color = Colors.BLACK)
            if (value.isEmpty()) {
                if (placeholder != null) {
                    drawText(
                        offset = IntOffset(6, node.height / 2 - 4),
                        width = node.width - 8,
                        text = placeholder,
                        color = Colors.LIGHT_GRAY
                    )
                }
            } else {
                drawText(
                    offset = IntOffset(6, node.height / 2 - 4),
                    width = node.width - 8,
                    text = value,
                    color = Colors.WHITE
                )
            }
        }
    }
}