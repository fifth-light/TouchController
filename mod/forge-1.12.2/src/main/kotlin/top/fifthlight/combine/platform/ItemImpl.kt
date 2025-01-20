package top.fifthlight.combine.platform

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import top.fifthlight.combine.data.DataComponentType
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.ItemSubclass
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.data.Item as CombineItem

@JvmInline
value class ItemImpl(
    val inner: Item
) : CombineItem {
    override val id: Identifier
        get() = ForgeRegistries.ITEMS.getKey(inner)?.toCombine()!!

    override val name: Text
        get() {
            val stack = ItemStack(inner)
            return TextImpl(TextComponentTranslation(stack.displayName))
        }

    override fun isSubclassOf(subclass: ItemSubclass): Boolean {
        val targetClazz = (subclass as ItemSubclassImpl<*>).clazz
        val itemClazz = inner.javaClass
        return itemClazz == targetClazz || itemClazz.superclass == targetClazz || itemClazz.interfaces.contains(
            targetClazz
        )
    }

    override fun containComponents(component: DataComponentType) = false
}