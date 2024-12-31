package top.fifthlight.combine.platform

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import net.minecraftforge.registries.ForgeRegistries
import top.fifthlight.combine.data.DataComponentType
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.Item

object FoodComponentImpl : DataComponentType {
    override val id: Identifier = Identifier.of("touchcontroller", "food")

    override val allItems: PersistentList<Item> by lazy {
        ForgeRegistries.ITEMS.filter { it.isEdible }.map { it.toCombine() }.toPersistentList()
    }
}