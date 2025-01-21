package top.fifthlight.combine.platform

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import net.minecraft.item.Item
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.registry.ForgeRegistries
import top.fifthlight.combine.data.*
import top.fifthlight.combine.data.Item as CombineItem
import top.fifthlight.combine.data.ItemStack as CombineItemStack

object ItemFactoryImpl : MetadataItemFactory {
    override fun createItem(id: Identifier): MetadataItem? {
        val item = ForgeRegistries.ITEMS.getValue(id.toMinecraft()) ?: return null
        return ItemImpl(item)
    }

    override fun createItem(
        id: Identifier,
        metadata: Int?
    ): MetadataItem? {
        val item = ForgeRegistries.ITEMS.getValue(id.toMinecraft()) ?: return null
        return ItemImpl(item, metadata)
    }

    override fun createItemStack(
        item: CombineItem,
        amount: Int
    ): MetadataItemStack {
        val itemImpl = (item as ItemImpl)
        val minecraftItem = itemImpl.inner
        val stack = ItemStack(minecraftItem, amount)
        itemImpl.metadata?.let { metadata ->
            stack.itemDamage = metadata
        }
        return ItemStackImpl(stack)
    }

    override fun createItemStack(id: Identifier, amount: Int): MetadataItemStack? {
        val item = ForgeRegistries.ITEMS.getValue(id.toMinecraft()) ?: return null
        val stack = ItemStack(item, amount)
        return ItemStackImpl(stack)
    }

    override val allItems: PersistentList<MetadataItem> by lazy {
        buildList {
            val list = NonNullList.create<ItemStack>()
            for (item in ForgeRegistries.ITEMS) {
                val tab = item.creativeTab
                if (tab == null) {
                    add(ItemImpl(item))
                    continue
                }
                item.getSubItems(tab, list)
                if (list.size <= 1) {
                    add(ItemImpl(item))
                } else {
                    list.distinctBy { it.itemDamage }.forEach { stack ->
                        add(
                            ItemImpl(
                                inner = stack.item,
                                metadata = stack.metadata,
                            )
                        )
                    }
                }
                list.clear()
            }
        }.toPersistentList()
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
