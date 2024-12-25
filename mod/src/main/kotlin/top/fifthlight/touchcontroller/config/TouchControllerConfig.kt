package top.fifthlight.touchcontroller.config

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Items
import top.fifthlight.data.IntOffset
import top.fifthlight.touchcontroller.control.*
import top.fifthlight.touchcontroller.layout.Align

val defaultUsableItemList = ItemList(
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
    val showPointers: Boolean = false,
    val enableTouchEmulation: Boolean = false,

    // Control
    val viewMovementSensitivity: Float = 495f,
    val viewHoldDetectThreshold: Int = 2,

    // Crosshair
    val crosshair: CrosshairConfig = CrosshairConfig(),

    // Items
    val usableItems: ItemList = defaultUsableItemList,
    val showCrosshairItems: ItemList = defaultShowCrosshairItemList,
)

@Serializable
data class CrosshairConfig(
    val radius: Int = 36,
    val outerRadius: Int = 2,
    val initialProgress: Float = .5f
)

typealias TouchControllerLayout = PersistentList<ControllerWidget>

val defaultTouchControllerLayout: TouchControllerLayout = persistentListOf(
    DPad(
        align = Align.LEFT_BOTTOM,
        offset = IntOffset(8, 8),
        opacity = 0.6f
    ),
    JumpButton(
        align = Align.RIGHT_BOTTOM,
        offset = IntOffset(42, 68),
        opacity = 0.6f
    ),
    AscendButton(
        align = Align.RIGHT_BOTTOM,
        offset = IntOffset(42, 116),
        opacity = 0.6f
    ),
    DescendButton(
        align = Align.RIGHT_BOTTOM,
        offset = IntOffset(42, 20),
        opacity = 0.6f
    ),
    PauseButton(
        align = Align.CENTER_TOP,
        offset = IntOffset(-9, 0),
        opacity = 0.6f
    ),
    ChatButton(
        align = Align.CENTER_TOP,
        offset = IntOffset(9, 0),
        opacity = 0.6f
    ),
    InventoryButton()
)