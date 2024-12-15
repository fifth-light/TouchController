package top.fifthlight.touchcontroller.config

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import net.minecraft.item.Items
import top.fifthlight.touchcontroller.control.*
import top.fifthlight.touchcontroller.ext.ItemsList
import top.fifthlight.touchcontroller.ext.ItemsListSerializer
import top.fifthlight.touchcontroller.layout.Align
import top.fifthlight.touchcontroller.proxy.data.IntOffset

val defaultUsableItems = persistentListOf(
    Items.FISHING_ROD,
    Items.SPYGLASS,
    Items.MAP,
    Items.SHIELD,
    Items.KNOWLEDGE_BOOK,
    Items.WRITABLE_BOOK,
    Items.WRITTEN_BOOK,
    Items.OMINOUS_BOTTLE,
    Items.ENDER_EYE,
    Items.ENDER_PEARL,
)

val defaultShowCrosshairItems = persistentListOf(
    Items.ENDER_PEARL,
)

@Serializable
data class TouchControllerConfig(
    val disableMouse: Boolean = true,
    val disableMouseLock: Boolean = false,
    val disableCrosshair: Boolean = true,
    val showPointers: Boolean = false,
    val enableTouchEmulation: Boolean = false,
    @Serializable(with = ItemsListSerializer::class)
    val usableItems: ItemsList = ItemsList(defaultUsableItems),
    val foodUsable: Boolean = true,
    val projectileUsable: Boolean = true,
    val rangedWeaponUsable: Boolean = true,
    val equippableUsable: Boolean = true,
    val bundleUsable: Boolean = true,
    @Serializable(with = ItemsListSerializer::class)
    val showCrosshairItems: ItemsList = ItemsList(defaultShowCrosshairItems),
    val projectileShowCrosshair: Boolean = true,
    val rangedWeaponShowCrosshair: Boolean = true,
    val crosshair: CrosshairConfig = CrosshairConfig()
)

@Serializable
data class CrosshairConfig(
    val radius: Int = 36,
    val outerRadius: Int = 2,
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
    )
)