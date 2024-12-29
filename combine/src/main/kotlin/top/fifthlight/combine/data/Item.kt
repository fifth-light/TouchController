package top.fifthlight.combine.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.collections.immutable.PersistentList

val LocalItemFactory = staticCompositionLocalOf<ItemFactory> { error("No ItemFactory in context") }

interface ItemFactory {
    fun createItem(id: Identifier): Item?
    fun createItemStack(item: Item, amount: Int): ItemStack
    fun createItemStack(id: Identifier, amount: Int): ItemStack?
    val allItems: PersistentList<Item>
    val rangedWeaponItems: PersistentList<Item>
    val projectileItems: PersistentList<Item>
}

@Immutable
interface Item {
    val id: Identifier
    val name: Text
    val isProjectile: Boolean
    val isRangedWeapon: Boolean
    fun containComponents(component: DataComponentType): Boolean

    @Composable
    fun toStack() = toStack(1)

    @Composable
    fun toStack(amount: Int) = LocalItemFactory.current.createItemStack(this, amount)

    companion object {
        @Composable
        fun of(id: Identifier) = LocalItemFactory.current.createItem(id)
    }
}
