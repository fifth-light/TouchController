package top.fifthlight.combine.platform

import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.Item
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.data.ItemStack as CombineItemStack

@JvmInline
value class ItemStackImpl(
    val inner: ItemStack
) : CombineItemStack {
    override val amount: Int
        get() = inner.count

    override val id: Identifier
        get() = Registry.ITEM.getKey(inner.item).get().value.toCombine()

    override val item: Item
        get() = ItemImpl(inner.item)

    override val isEmpty: Boolean
        get() = inner.isEmpty

    override val name: Text
        get() = TextImpl(inner.name)

    override fun withAmount(amount: Int) = ItemStackImpl(inner.copy().apply {
        count = amount
    })
}