package top.fifthlight.combine.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalItemStackFactory = staticCompositionLocalOf<ItemStackFactory> { error("No ItemStackFactory in context") }

interface ItemStackFactory {
    fun create(id: Identifier, amount: Int): ItemStack?
}

interface ItemStack {
    var amount: Int
    val id: Identifier

    companion object {
        @Composable
        fun of(id: Identifier, amount: Int) = LocalItemStackFactory.current.create(id, amount)
    }
}
