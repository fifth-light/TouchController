package top.fifthlight.combine.platform

import net.minecraft.item.Item
import net.minecraft.util.registry.Registry
import top.fifthlight.combine.data.DataComponentType
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.ItemSubclass
import top.fifthlight.combine.data.Item as CombineItem

@JvmInline
value class ItemImpl(
    val inner: Item
) : CombineItem {
    override val id: Identifier
        get() = Registry.ITEM.getKey(inner).get().value.toCombine()

    override fun isSubclassOf(subclass: ItemSubclass): Boolean {
        val targetClazz = (subclass as ItemSubclassImpl<*>).clazz
        val itemClazz = inner.javaClass
        return itemClazz == targetClazz || itemClazz.superclass == targetClazz || itemClazz.interfaces.contains(
            targetClazz
        )
    }

    override fun containComponents(component: DataComponentType) = if (component is FoodComponentImpl) {
        inner.isFood
    } else {
        false
    }
}