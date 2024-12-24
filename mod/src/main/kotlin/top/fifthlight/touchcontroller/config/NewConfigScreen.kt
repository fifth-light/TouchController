package top.fifthlight.touchcontroller.config

import net.minecraft.client.gui.screen.Screen
import top.fifthlight.combine.platform.CombineScreen
import top.fifthlight.touchcontroller.ui.model.ConfigScreenViewModel
import top.fifthlight.touchcontroller.ui.screen.config.ConfigScreen

private class NewConfigScreen(parent: Screen?) : CombineScreen(net.minecraft.text.Text.empty(), parent) {
    private val viewModel = ConfigScreenViewModel(coroutineContext)

    override fun init() {
        setContent {
            ConfigScreen(viewModel)
        }
        super.init()
    }
}

fun openNewConfigScreen(parent: Screen): Screen = NewConfigScreen(parent)
