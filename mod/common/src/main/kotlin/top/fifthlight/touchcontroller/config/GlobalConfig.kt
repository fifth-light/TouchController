package top.fifthlight.touchcontroller.config

import kotlinx.serialization.Serializable
import top.fifthlight.touchcontroller.gal.DefaultItemListProvider

@Serializable
data class GlobalConfig(
    // Global
    val disableMouseMove: Boolean = true,
    val disableMouseClick: Boolean = true,
    val disableMouseLock: Boolean = false,
    val disableCrosshair: Boolean = true,
    val disableHotBarKey: Boolean = false,
    val vibration: Boolean = true,
    val quickHandSwap: Boolean = false,
    val splitControls: Boolean = false,
    val disableTouchGesture: Boolean = false,

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
        fun default(itemListProvider: DefaultItemListProvider) = GlobalConfig(
            usableItems = itemListProvider.usableItems,
            showCrosshairItems = itemListProvider.showCrosshairItems,
        )
    }
}

@Serializable
data class CrosshairConfig(
    val radius: Int = 36,
    val outerRadius: Int = 2,
    val initialProgress: Float = .5f
)
