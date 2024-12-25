package top.fifthlight.touchcontroller.ui.state

import top.fifthlight.touchcontroller.config.TouchControllerConfig
import top.fifthlight.touchcontroller.ui.view.config.category.ConfigCategory

data class ConfigScreenState(
    val config: TouchControllerConfig,
    val selectedCategory: ConfigCategory,
)