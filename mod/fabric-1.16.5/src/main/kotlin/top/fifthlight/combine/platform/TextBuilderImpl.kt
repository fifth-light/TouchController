package top.fifthlight.combine.platform

import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import top.fifthlight.combine.data.TextBuilder
import top.fifthlight.combine.data.Text as CombineText

class TextBuilderImpl(
    private val text: MutableText = LiteralText(""),
    private val style: Style = Style.EMPTY,
) : TextBuilder {
    override fun bold(bold: Boolean, block: TextBuilder.() -> Unit) {
        block(
            TextBuilderImpl(
                text = text,
                style = style.withBold(bold),
            )
        )
    }

    override fun underline(underline: Boolean, block: TextBuilder.() -> Unit) {
        block(
            TextBuilderImpl(
                text = text,
                style = style.withUnderline(underline),
            )
        )
    }

    override fun italic(italic: Boolean, block: TextBuilder.() -> Unit) {
        block(
            TextBuilderImpl(
                text = text,
                style = style.withItalic(italic),
            )
        )
    }

    override fun append(string: String) {
        this.text.append(string)
    }

    override fun appendWithoutStyle(text: CombineText) {
        this.text.append(LiteralText(text.toMinecraft().string).setStyle(style))
    }

    fun build(): CombineText = TextImpl(text)
}
