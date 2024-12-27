package top.fifthlight.touchcontroller.ui.screen.config

import kotlinx.collections.immutable.PersistentList
import top.fifthlight.combine.data.DataComponentType
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.combine.screen.ScreenFactory
import top.fifthlight.touchcontroller.ui.model.ComponentListScreenViewModel
import top.fifthlight.touchcontroller.ui.view.config.ComponentListScreen

fun openComponentListScreen(
    screenFactory: ScreenFactory,
    textFactory: TextFactory,
    initialList: PersistentList<DataComponentType>,
    onListChanged: (PersistentList<DataComponentType>) -> Unit,
) {
    screenFactory.openScreen(
        title = textFactory.empty(),
        viewModelFactory = {
            ComponentListScreenViewModel(it, initialList, onListChanged)
        },
        content = {
            ComponentListScreen(it)
        }
    )
}