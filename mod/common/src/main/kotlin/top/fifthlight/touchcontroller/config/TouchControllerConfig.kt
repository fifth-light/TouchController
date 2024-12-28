package top.fifthlight.touchcontroller.config

import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import top.fifthlight.combine.data.ItemFactory

/*val defaultUsableItemList = ItemList(
    whitelist = persistentListOf(
        Items.FISHING_ROD,
        Items.SPYGLASS,
        Items.MAP,
        Items.SHIELD,
        Items.KNOWLEDGE_BOOK,
        Items.WRITABLE_BOOK,
        Items.WRITTEN_BOOK,
        Items.ENDER_EYE,
        Items.ENDER_PEARL,
    ),
    components = persistentListOf(
        DataComponentTypes.EQUIPPABLE,
        DataComponentTypes.BUNDLE_CONTENTS,
        DataComponentTypes.CONSUMABLE,
        DataComponentTypes.FOOD,
    ),
    projectile = true,
    rangedWeapon = true,
)

val defaultShowCrosshairItemList = ItemList(
    whitelist = persistentListOf(
        Items.ENDER_PEARL,
    ),
    blacklist = persistentListOf(
        Items.FIREWORK_ROCKET,
    ),
    projectile = true,
    rangedWeapon = true,
)*/
val defaultUsableItemList = ItemList(
    whitelist = persistentListOf(),
    components = persistentListOf(),
    projectile = true,
    rangedWeapon = true,
)

val defaultShowCrosshairItemList = ItemList(
    whitelist = persistentListOf(),
    blacklist = persistentListOf(),
    projectile = true,
    rangedWeapon = true,
)

@Serializable
data class TouchControllerConfig(
    // Global
    val disableMouseMove: Boolean = true,
    val disableMouseClick: Boolean = true,
    val disableMouseLock: Boolean = false,
    val disableCrosshair: Boolean = true,
    val disableHotBarKey: Boolean = false,
    val vibration: Boolean = true,
    val quickHandSwap: Boolean = false,

    // Control
    val viewMovementSensitivity: Float = 495f,
    val viewHoldDetectThreshold: Int = 2,

    // Crosshair
    val crosshair: CrosshairConfig = CrosshairConfig(),

    // Debug
    val showPointers: Boolean = false,
    val enableTouchEmulation: Boolean = false,

    // Items
    val usableItems: ItemList,
    val showCrosshairItems: ItemList,
) {
    companion object {
        fun default(itemFactory: ItemFactory) = TouchControllerConfig(
            usableItems = ItemList(),
            showCrosshairItems = ItemList()
        )
    }
}

@Serializable
data class CrosshairConfig(
    val radius: Int = 36,
    val outerRadius: Int = 2,
    val initialProgress: Float = .5f
)
