package top.fifthlight.combine.platform

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import top.fifthlight.combine.data.Text as CombineText

@JvmInline
value class TextImpl(
    val inner: Component
) : CombineText {
    override val string: String
        get() = inner.string

    override fun bold(): CombineText = TextImpl(MutableComponent.create(inner.contents).setStyle(STYLE_BOLD))

    override fun underline(): CombineText = TextImpl(MutableComponent.create(inner.contents).setStyle(STYLE_UNDERLINE))

    override fun italic(): CombineText = TextImpl(MutableComponent.create(inner.contents).setStyle(STYLE_ITALIC))

    override fun copy(): CombineText = TextImpl(inner.copy())

    override fun plus(other: CombineText): CombineText = TextImpl(inner.copy().append(other.toMinecraft()))

    companion object {
        val EMPTY = TextImpl(Component.empty())
        private val STYLE_BOLD = Style.EMPTY.withBold(true)
        private val STYLE_UNDERLINE = Style.EMPTY.withUnderlined(true)
        private val STYLE_ITALIC = Style.EMPTY.withItalic(true)
    }
}
