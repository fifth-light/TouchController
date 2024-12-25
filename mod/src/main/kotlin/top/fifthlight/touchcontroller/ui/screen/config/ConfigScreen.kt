package top.fifthlight.touchcontroller.ui.screen.config

import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text.empty
import top.fifthlight.combine.platform.CombineScreen
import top.fifthlight.touchcontroller.ui.model.ConfigScreenViewModel
import top.fifthlight.touchcontroller.ui.view.config.ConfigScreen

private class ConfigScreen(parent: Screen?) : CombineScreen(empty(), parent) {
    private val viewModel = ConfigScreenViewModel(this)

    override fun init() {
        setContent {
            ConfigScreen(viewModel)
        }
        super.init()
    }
}

fun openConfigScreen(parent: Screen): Screen = ConfigScreen(parent)
