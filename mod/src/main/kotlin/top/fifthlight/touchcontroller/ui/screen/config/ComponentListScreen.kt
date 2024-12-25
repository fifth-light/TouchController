package top.fifthlight.touchcontroller.ui.screen.config

import kotlinx.collections.immutable.PersistentList
import net.minecraft.client.gui.screen.Screen
import net.minecraft.component.ComponentType
import net.minecraft.text.Text
import top.fifthlight.combine.platform.CombineScreen
import top.fifthlight.touchcontroller.ui.model.ComponentListScreenViewModel
import top.fifthlight.touchcontroller.ui.view.config.ComponentListScreen

class ComponentListScreen(
    parent: Screen?,
    initialList: PersistentList<ComponentType<*>>,
    onListChanged: (PersistentList<ComponentType<*>>) -> Unit,
) : CombineScreen(Text.empty(), parent) {
    private val viewModel = ComponentListScreenViewModel(this, initialList, onListChanged)

    override fun init() {
        setContent {
            ComponentListScreen(viewModel)
        }
        super.init()
    }
}