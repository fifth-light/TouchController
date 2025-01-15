package top.fifthlight.touchcontroller.gal

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import net.minecraft.init.Items
import top.fifthlight.combine.platform.ItemFactoryImpl
import top.fifthlight.combine.platform.ItemImpl
import top.fifthlight.touchcontroller.config.ItemList

object DefaultItemListProviderImpl : DefaultItemListProvider {
    override val usableItems = ItemList(
        whitelist = persistentListOf(
            ItemImpl(Items.FISHING_ROD),
            ItemImpl(Items.MAP),
            ItemImpl(Items.SHIELD),
            ItemImpl(Items.KNOWLEDGE_BOOK),
            ItemImpl(Items.WRITABLE_BOOK),
            ItemImpl(Items.WRITTEN_BOOK),
            ItemImpl(Items.ENDER_EYE),
            ItemImpl(Items.ENDER_PEARL),
            ItemImpl(Items.POTIONITEM),
            ItemImpl(Items.SNOWBALL),
            ItemImpl(Items.EGG),
            ItemImpl(Items.SPLASH_POTION),
            ItemImpl(Items.LINGERING_POTION),
            ItemImpl(Items.EXPERIENCE_BOTTLE),
        ),
        subclasses = persistentSetOf(
            ItemFactoryImpl.armorSubclass,
        ),
    )

    override val showCrosshairItems = ItemList(
        whitelist = persistentListOf(
            ItemImpl(Items.EGG),
            ItemImpl(Items.SNOWBALL),
            ItemImpl(Items.BOW),
            ItemImpl(Items.SPLASH_POTION),
            ItemImpl(Items.LINGERING_POTION),
            ItemImpl(Items.EXPERIENCE_BOTTLE),
        ),
    )
}