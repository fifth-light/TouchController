package top.fifthlight.combine.widget.base

import androidx.compose.runtime.Composable
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.paint.GuiTexture
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntRect
import top.fifthlight.data.IntSize

@Composable
fun GuiTexture(
    texture: GuiTexture,
    size: IntSize,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier,
        measurePolicy = { _, constraints ->
            layout(
                width = size.width.coerceIn(constraints.minWidth, constraints.maxWidth),
                height = size.height.coerceIn(constraints.minHeight, constraints.maxHeight)
            ) {}
        },
        renderer = {
            canvas.drawGuiTexture(
                texture = texture,
                dstRect = IntRect(offset = IntOffset.ZERO, size = size),
            )
        }
    )
}