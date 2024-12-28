package top.fifthlight.combine.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalTextFactory = staticCompositionLocalOf<TextFactory> { error("No TextFactory in context") }

interface TextFactory {
    fun literal(string: String): Text
    fun of(identifier: Identifier): Text
    fun empty(): Text
    fun format(identifier: Identifier, vararg arguments: Any): Text
    fun toNative(text: Text): Any
}

interface Text {
    val string: String

    companion object {
        @Composable
        fun translatable(identifier: Identifier) = LocalTextFactory.current.of(identifier)

        @Composable
        fun empty() = LocalTextFactory.current.empty()

        @Composable
        fun literal(string: String) = LocalTextFactory.current.literal(string)
    }
}
