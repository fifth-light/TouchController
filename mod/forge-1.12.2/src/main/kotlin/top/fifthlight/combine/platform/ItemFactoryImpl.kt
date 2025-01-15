package top.fifthlight.combine.platform

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import net.minecraft.item.Item
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.registry.ForgeRegistries
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.ItemFactory
import top.fifthlight.combine.data.ItemSubclass
import top.fifthlight.combine.data.Item as CombineItem
import top.fifthlight.combine.data.ItemStack as CombineItemStack

object ItemFactoryImpl : ItemFactory {
    override fun createItem(id: Identifier): CombineItem? {
        val item = ForgeRegistries.ITEMS.getValue(id.toMinecraft()) ?: return null
        return ItemImpl(item)
    }

    override fun createItemStack(
        item: CombineItem,
        amount: Int
    ): CombineItemStack {
        val minecraftItem = (item as ItemImpl).inner
        val stack = ItemStack(minecraftItem, amount)
        return ItemStackImpl(stack)
    }

    override fun createItemStack(id: Identifier, amount: Int): CombineItemStack? {
        val item = ForgeRegistries.ITEMS.getValue(id.toMinecraft()) ?: return null
        val stack = ItemStack(item, amount)
        return ItemStackImpl(stack)
    }

    override val allItems: PersistentList<CombineItem> by lazy {
        Item.REGISTRY.map(Item::toCombine).toPersistentList()
    }

    val armorSubclass = ItemSubclassImpl(
        name = TextImpl(TextComponentString("Armor")),
        configId = "ItemArmor",
        clazz = ItemArmor::class.java
    )

    override val subclasses: PersistentList<ItemSubclass> = persistentListOf(
        armorSubclass,
    )
}

fun Item.toCombine() = ItemImpl(this)
fun ItemStack.toCombine() = ItemStackImpl(this)
fun CombineItem.toVanilla() = (this as ItemImpl).inner
fun CombineItemStack.toVanilla() = (this as ItemStackImpl).inner
