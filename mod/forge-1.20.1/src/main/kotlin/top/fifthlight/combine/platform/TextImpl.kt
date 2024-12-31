package top.fifthlight.combine.platform

import net.minecraft.network.chat.Component
import top.fifthlight.combine.data.Text as CombineText

@JvmInline
value class TextImpl(
    val inner: Component
) : CombineText {
    override val string: String
        get() = inner.string

    companion object {
        val EMPTY = TextImpl(Component.empty())
    }
}
