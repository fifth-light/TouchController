package top.fifthlight.combine.platform

import net.minecraft.text.Text
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.combine.data.Text as CombineText

object TextFactoryImpl : TextFactory {
    override fun literal(string: String) = TextImpl(Text.literal(string))

    override fun of(identifier: Identifier) = TextImpl(Text.of(identifier.toMinecraft()))

    override fun empty() = TextImpl.EMPTY

    override fun format(identifier: Identifier, vararg arguments: Any) =
        TextImpl(Text.translatable(identifier.toString(), *arguments))
}

fun CombineText.toMinecraft() = (this as TextImpl).inner