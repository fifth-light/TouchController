package top.fifthlight.combine.platform

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import top.fifthlight.combine.data.TextBuilder
import top.fifthlight.combine.data.Text as CombineText

class TextBuilderImpl(
    private val text: MutableComponent = Component.empty(),
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
                style = style.withUnderlined(underline),
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
        this.text.append(MutableComponent.create(text.toMinecraft().contents).setStyle(style))
    }

    fun build(): CombineText = TextImpl(text)
}
