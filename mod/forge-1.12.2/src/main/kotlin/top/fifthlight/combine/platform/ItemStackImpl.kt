package top.fifthlight.combine.platform

import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.registry.ForgeRegistries
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.MetadataItemStack
import top.fifthlight.combine.data.Text

@JvmInline
value class ItemStackImpl(
    val inner: ItemStack
) : MetadataItemStack {
    override val amount: Int
        get() = inner.count

    override val id: Identifier
        get() = ForgeRegistries.ITEMS.getKey(inner.item)?.toCombine()!!

    override val metadata: Int
        get() = inner.itemDamage

    override val item: ItemImpl
        get() = ItemImpl(
            inner = inner.item,
            metadata = inner.itemDamage,
        )

    override val isEmpty: Boolean
        get() = inner.isEmpty

    override val name: Text
        get() = TextImpl(TextComponentString(inner.getDisplayName()))

    override fun withAmount(amount: Int) = ItemStackImpl(inner.copy().apply { count = amount })
}