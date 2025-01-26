package top.fifthlight.combine.platform

import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.TextBuilder
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.combine.data.Text as CombineText

object TextFactoryImpl : TextFactory {
    override fun build(block: TextBuilder.() -> Unit) = TextBuilderImpl().apply(block).build()

    override fun literal(string: String) = TextImpl(Text.of(string))

    private fun transformIdentifier(identifier: Identifier) = when (identifier) {
        is Identifier.Namespaced -> "${identifier.namespace}.${identifier.id}"
        is Identifier.Vanilla -> identifier.id
    }

    override fun of(identifier: Identifier) = TextImpl(TranslatableText(transformIdentifier(identifier)))

    override fun empty() = TextImpl.EMPTY

    override fun format(identifier: Identifier, vararg arguments: Any?) =
        TextImpl(TranslatableText(transformIdentifier(identifier), *arguments))

    override fun toNative(text: CombineText): Any = (text as TextImpl).inner
}

fun CombineText.toMinecraft() = (this as TextImpl).inner