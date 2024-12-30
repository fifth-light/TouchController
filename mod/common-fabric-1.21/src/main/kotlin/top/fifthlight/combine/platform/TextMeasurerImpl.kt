package top.fifthlight.combine.platform

import net.minecraft.client.font.TextRenderer
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.paint.TextMeasurer
import top.fifthlight.data.IntSize

class TextMeasurerImpl(private val textRenderer: TextRenderer) : TextMeasurer {
    override fun measure(text: String) = IntSize(
        width = textRenderer.getWidth(text),
        height = 9
    )

    override fun measure(text: String, maxWidth: Int) = IntSize(
        width = textRenderer.getWidth(text).coerceAtMost(maxWidth),
        height = textRenderer.getWrappedLinesHeight(text, maxWidth)
    )

    override fun measure(text: Text) = IntSize(
        width = textRenderer.getWidth(text.toMinecraft()),
        height = 9
    )

    override fun measure(text: Text, maxWidth: Int): IntSize {
        val inner = text.toMinecraft()
        return IntSize(
            width = textRenderer.getWidth(inner).coerceAtMost(maxWidth),
            height = textRenderer.getWrappedLinesHeight(inner, maxWidth)
        )
    }
}