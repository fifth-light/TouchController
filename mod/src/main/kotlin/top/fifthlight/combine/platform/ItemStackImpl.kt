package top.fifthlight.combine.platform

import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.ItemStack as CombineItemStack

class ItemStackImpl(
    val inner: ItemStack
) : CombineItemStack {
    override var amount: Int
        get() = inner.count
        set(value) {
            inner.count = value
        }

    override val id: Identifier
        get() = Registries.ITEM.getKey(inner.item).get().value.toCombine()
}