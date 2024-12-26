package top.fifthlight.touchcontroller.ui.state

import top.fifthlight.touchcontroller.config.TouchControllerConfig
import top.fifthlight.touchcontroller.config.TouchControllerLayout
import top.fifthlight.touchcontroller.ui.view.config.category.ConfigCategory
import top.fifthlight.touchcontroller.ui.view.config.category.GlobalCategory

enum class LayoutPanelState {
    LAYOUT,
    LAYERS,
    WIDGETS,
    PRESETS,
}

data class ConfigScreenState(
    val config: TouchControllerConfig,
    val layout: TouchControllerLayout,
    val selectedLayer: Int,
    val selectedCategory: ConfigCategory = GlobalCategory,
    val layoutPanelState: LayoutPanelState = LayoutPanelState.LAYOUT,
)
