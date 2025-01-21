package top.fifthlight.combine.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
interface ItemStack {
    val amount: Int
    val id: Identifier
    val item: Item
    val isEmpty: Boolean
    val name: Text

    fun withAmount(amount: Int): ItemStack

    companion object {
        @Composable
        fun of(id: Identifier, amount: Int = 1) = LocalItemFactory.current.createItemStack(id, amount)
    }
}

interface MetadataItemStack : ItemStack {
    val metadata: Int
    override val item: MetadataItem
}
