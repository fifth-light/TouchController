package top.fifthlight.touchcontroller.gal

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Items
import top.fifthlight.combine.platform.DataComponentTypeImpl
import top.fifthlight.combine.platform.ItemFactoryImpl
import top.fifthlight.combine.platform.ItemImpl
import top.fifthlight.touchcontroller.config.ItemList

object DefaultItemListProviderImpl : DefaultItemListProvider {
    override val usableItems = ItemList(
        whitelist = persistentListOf(
            ItemImpl(Items.FISHING_ROD),
            ItemImpl(Items.SPYGLASS),
            ItemImpl(Items.MAP),
            ItemImpl(Items.SHIELD),
            ItemImpl(Items.KNOWLEDGE_BOOK),
            ItemImpl(Items.WRITABLE_BOOK),
            ItemImpl(Items.WRITTEN_BOOK),
            ItemImpl(Items.ENDER_EYE),
            ItemImpl(Items.ENDER_PEARL),
        ),
        blacklist = persistentListOf(
            ItemImpl(Items.ARROW),
            ItemImpl(Items.FIRE_CHARGE),
            ItemImpl(Items.SPECTRAL_ARROW),
            ItemImpl(Items.TIPPED_ARROW),
            ItemImpl(Items.FIREWORK_ROCKET),
        ),
        subclasses = persistentSetOf(
            ItemFactoryImpl.rangedWeaponSubclass,
            ItemFactoryImpl.projectileSubclass,
        ),
        components = persistentListOf(
            DataComponentTypeImpl(DataComponentTypes.FOOD),
            DataComponentTypeImpl(DataComponentTypes.BUNDLE_CONTENTS),
            DataComponentTypeImpl(DataComponentTypes.CONSUMABLE),
            DataComponentTypeImpl(DataComponentTypes.EQUIPPABLE),
        )
    )

    override val showCrosshairItems = ItemList(
        blacklist = persistentListOf(
            ItemImpl(Items.FIREWORK_ROCKET),
            ItemImpl(Items.ARROW),
            ItemImpl(Items.FIRE_CHARGE),
            ItemImpl(Items.SPECTRAL_ARROW),
            ItemImpl(Items.TIPPED_ARROW),
        ),
        subclasses = persistentSetOf(
            ItemFactoryImpl.rangedWeaponSubclass,
            ItemFactoryImpl.projectileSubclass,
        )
    )
}