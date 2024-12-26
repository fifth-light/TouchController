package top.fifthlight.combine.platform

import net.minecraft.item.Item
import net.minecraft.registry.Registries
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.Item as CombineItem

data class ItemImpl(
    val inner: Item
) : CombineItem {
    override val id: Identifier by lazy {
        Registries.ITEM.getKey(inner).get().value.toCombine()
    }

    override val name: String
        get() = inner.name.string
}