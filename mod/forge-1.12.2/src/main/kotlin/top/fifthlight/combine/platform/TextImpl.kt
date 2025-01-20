package top.fifthlight.combine.platform

import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import top.fifthlight.combine.data.Text as CombineText

@JvmInline
value class TextImpl(
    val inner: ITextComponent,
) : CombineText {
    override val string: String
        get() = inner.unformattedText

    override fun bold(): CombineText = TextImpl(TextComponentString(STYLE_BOLD.formattingCode + string))

    override fun underline(): CombineText = TextImpl(TextComponentString(STYLE_UNDERLINE.formattingCode + string))

    override fun italic(): CombineText = TextImpl(TextComponentString(STYLE_ITALIC.formattingCode + string))

    override fun copy(): CombineText = TextImpl(inner.createCopy())

    override fun plus(other: CombineText): CombineText = TextImpl(inner.createCopy().appendSibling(other.toMinecraft()))

    companion object {
        val EMPTY = TextImpl(TextComponentString(""))
        private val STYLE_BOLD = Style().apply { bold = true }
        private val STYLE_UNDERLINE = Style().apply { underlined = true }
        private val STYLE_ITALIC = Style().apply { italic = true }
    }
}
