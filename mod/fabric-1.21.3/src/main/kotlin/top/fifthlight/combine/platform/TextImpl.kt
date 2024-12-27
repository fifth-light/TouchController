package top.fifthlight.combine.platform

import net.minecraft.text.Text
import top.fifthlight.combine.data.Text as CombineText

@JvmInline
value class TextImpl(
    val inner: Text
) : CombineText {
    override val string: String
        get() = inner.string

    companion object {
        val EMPTY = TextImpl(Text.empty())
    }
}
