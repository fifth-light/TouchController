package top.fifthlight.combine.platform

import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import top.fifthlight.combine.data.Text as CombineText

@JvmInline
value class TextImpl(
    val inner: Text
) : CombineText {
    override val string: String
        get() = inner.string

    override fun bold(): CombineText = TextImpl(LiteralText(inner.string).setStyle(STYLE_BOLD))

    override fun underline(): CombineText = TextImpl(LiteralText(inner.string).setStyle(STYLE_UNDERLINE))

    override fun italic(): CombineText = TextImpl(LiteralText(inner.string).setStyle(STYLE_ITALIC))

    override fun copy(): CombineText = TextImpl(inner.copy())

    override fun plus(other: CombineText): CombineText = TextImpl(inner.copy().append(other.toMinecraft()))

    companion object {
        val EMPTY = TextImpl(LiteralText.EMPTY)
        private val STYLE_BOLD = Style.EMPTY.withBold(true)
        private val STYLE_UNDERLINE = Style.EMPTY.withUnderline(true)
        private val STYLE_ITALIC = Style.EMPTY.withItalic(true)
    }
}
