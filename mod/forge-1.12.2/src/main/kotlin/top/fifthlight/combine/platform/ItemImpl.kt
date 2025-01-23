package top.fifthlight.combine.platform

import net.minecraft.item.Item
import net.minecraftforge.fml.common.registry.ForgeRegistries
import top.fifthlight.combine.data.DataComponentType
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.ItemSubclass
import top.fifthlight.combine.data.MetadataItem
import top.fifthlight.combine.data.Item as CombineItem

data class ItemImpl(
    val inner: Item,
    override val metadata: Int? = null,
) : MetadataItem {
    init {
        require(metadata != -1)
    }

    override val id: Identifier
        get() = ForgeRegistries.ITEMS.getKey(inner)?.toCombine()!!

    override fun isSubclassOf(subclass: ItemSubclass): Boolean {
        val targetClazz = (subclass as ItemSubclassImpl<*>).clazz
        val itemClazz = inner.javaClass
        return itemClazz == targetClazz || itemClazz.superclass == targetClazz || itemClazz.interfaces.contains(
            targetClazz
        )
    }

    override fun containComponents(component: DataComponentType) = false

    override fun matches(other: CombineItem): Boolean {
        val otherItem = (other as ItemImpl)
        if (this.inner != otherItem.inner) {
            return false
        }
        return if (this.metadata == null || otherItem.metadata == null) {
            true
        } else {
            this.metadata == otherItem.metadata
        }
    }
}