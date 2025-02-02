package top.fifthlight.touchcontroller.ui.state

import kotlinx.collections.immutable.plus
import top.fifthlight.touchcontroller.about.AboutInfo
import top.fifthlight.touchcontroller.config.ControllerLayout
import top.fifthlight.touchcontroller.config.GlobalConfig
import top.fifthlight.touchcontroller.config.LayoutPresets
import top.fifthlight.touchcontroller.config.defaultPresets
import top.fifthlight.touchcontroller.control.ControllerWidget
import top.fifthlight.touchcontroller.ui.view.config.category.ConfigCategory
import top.fifthlight.touchcontroller.ui.view.config.category.GlobalCategory

enum class LayoutPanelState {
    LAYOUT,
    LAYERS,
    WIDGETS,
    PRESETS,
}

data class ConfigScreenState(
    val config: GlobalConfig,
    val layout: ControllerLayout,
    val presets: LayoutPresets,
    val showExitDialog: Boolean = false,
    val selectedLayer: Int = 0,
    val selectedCategory: ConfigCategory = GlobalCategory,
    val layoutPanelState: LayoutPanelState = LayoutPanelState.LAYOUT,
    val selectedWidget: Int = -1,
    val selectedPreset: Int = 0,
    val defaultOpacity: Float = .6f,
    val copiedWidget: ControllerWidget? = null,
    val aboutInfo: AboutInfo? = null,
) {
    val allPresets = defaultPresets.presets + presets.presets
}
