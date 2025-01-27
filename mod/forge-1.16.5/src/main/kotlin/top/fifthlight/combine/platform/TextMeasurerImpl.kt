package top.fifthlight.combine.platform

import net.minecraft.client.gui.FontRenderer
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.paint.TextMeasurer
import top.fifthlight.data.IntSize

class TextMeasurerImpl(private val textRenderer: FontRenderer) : TextMeasurer {
    override fun measure(text: String) = IntSize(
        width = textRenderer.width(text),
        height = textRenderer.lineHeight,
    )

    override fun measure(text: String, maxWidth: Int) = IntSize(
        width = textRenderer.width(text).coerceAtMost(maxWidth),
        height = textRenderer.wordWrapHeight(text, maxWidth)
    )

    override fun measure(text: Text) = IntSize(
        width = textRenderer.width(text.toMinecraft()),
        height = textRenderer.lineHeight,
    )

    override fun measure(text: Text, maxWidth: Int): IntSize {
        val inner = text.toMinecraft()
        return IntSize(
            width = textRenderer.width(inner).coerceAtMost(maxWidth),
            height = textRenderer.wordWrapHeight(inner.string, maxWidth)
        )
    }
}