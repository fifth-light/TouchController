package top.fifthlight.combine.platform

import net.minecraft.client.gui.FontRenderer
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.paint.TextMeasurer
import top.fifthlight.data.IntSize

class TextMeasurerImpl(private val textRenderer: FontRenderer) : TextMeasurer {
    override fun measure(text: String) = IntSize(
        width = textRenderer.getStringWidth(text),
        height = 9
    )

    override fun measure(text: String, maxWidth: Int) = IntSize(
        width = textRenderer.getStringWidth(text).coerceAtMost(maxWidth),
        height = textRenderer.getWordWrappedHeight(text, maxWidth)
    )

    override fun measure(text: Text) = IntSize(
        width = textRenderer.getStringWidth(text.toMinecraft().formattedText),
        height = 9
    )

    override fun measure(text: Text, maxWidth: Int): IntSize {
        val inner = text.toMinecraft().formattedText
        return IntSize(
            width = textRenderer.getStringWidth(inner).coerceAtMost(maxWidth),
            height = textRenderer.getWordWrappedHeight(inner, maxWidth)
        )
    }
}