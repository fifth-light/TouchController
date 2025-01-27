package top.fifthlight.combine.platform

import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.Style
import top.fifthlight.combine.data.Text as CombineText

@JvmInline
value class TextImpl(
    val inner: ITextComponent
) : CombineText {
    override val string: String
        get() = inner.string

    override fun bold(): CombineText = TextImpl(StringTextComponent(inner.string).setStyle(STYLE_BOLD))

    override fun underline(): CombineText = TextImpl(StringTextComponent(inner.string).setStyle(STYLE_UNDERLINE))

    override fun italic(): CombineText = TextImpl(StringTextComponent(inner.string).setStyle(STYLE_ITALIC))

    override fun copy(): CombineText = TextImpl(inner.copy())

    override fun plus(other: CombineText): CombineText = TextImpl(inner.copy().append(other.toMinecraft()))

    companion object {
        val EMPTY = TextImpl(StringTextComponent.EMPTY)
        private val STYLE_BOLD = Style.EMPTY.withBold(true)
        private val STYLE_UNDERLINE = Style.EMPTY.withUnderlined(true)
        private val STYLE_ITALIC = Style.EMPTY.withItalic(true)
    }
}
