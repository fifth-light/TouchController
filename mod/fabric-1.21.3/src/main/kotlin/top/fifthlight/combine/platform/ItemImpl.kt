package top.fifthlight.combine.platform

import net.minecraft.item.Item
import net.minecraft.item.ProjectileItem
import net.minecraft.item.RangedWeaponItem
import net.minecraft.registry.Registries
import top.fifthlight.combine.data.DataComponentType
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.Item as CombineItem

@JvmInline
value class ItemImpl(
    val inner: Item
) : CombineItem {
    override val id: Identifier
        get() = Registries.ITEM.getKey(inner).get().value.toCombine()

    override val name: TextImpl
        get() = TextImpl(inner.name)

    override val isProjectile: Boolean
        get() = inner is ProjectileItem

    override val isRangedWeapon: Boolean
        get() = inner is RangedWeaponItem

    override fun containComponents(component: DataComponentType) =
        inner.components.contains((component as DataComponentTypeImpl).inner)
}