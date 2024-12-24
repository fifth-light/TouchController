package top.fifthlight.combine.platform

import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.ItemStackFactory
import kotlin.jvm.optionals.getOrNull
import top.fifthlight.combine.data.ItemStack as CombineItemStack

object ItemStackFactoryImpl : ItemStackFactory {
    override fun create(id: Identifier, amount: Int): CombineItemStack? {
        val item = Registries.ITEM.getOptionalValue(id.toMinecraft()).getOrNull() ?: return null
        val stack = ItemStack(item, amount)
        return ItemStackImpl(stack)
    }
}