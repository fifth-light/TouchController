package top.fifthlight.combine.platform

import net.minecraft.client.font.TextRenderer
import top.fifthlight.combine.paint.TextMeasurer
import top.fifthlight.data.IntSize

class MinecraftTextMeasurer(private val textRenderer: TextRenderer) : TextMeasurer {
    override fun measure(text: String) = IntSize(
        width = textRenderer.getWidth(text),
        height = 9
    )

    override fun measure(text: String, maxWidth: Int) = IntSize(
        width = textRenderer.getWidth(text).coerceAtMost(maxWidth),
        height = textRenderer.getWrappedLinesHeight(text, maxWidth)
    )
}