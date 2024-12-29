package top.fifthlight.touchcontroller.ui.screen.config

import kotlinx.collections.immutable.PersistentList
import top.fifthlight.combine.data.Item
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.combine.screen.ScreenFactory
import top.fifthlight.touchcontroller.ui.model.ItemListScreenViewModel
import top.fifthlight.touchcontroller.ui.view.config.ItemListScreen

fun openItemListScreen(
    screenFactory: ScreenFactory,
    textFactory: TextFactory,
    initialList: PersistentList<Item>,
    onListChanged: (PersistentList<Item>) -> Unit,
) {
    screenFactory.openScreen(
        title = textFactory.empty(),
        viewModelFactory = { scope, _ ->
            ItemListScreenViewModel(scope, initialList, onListChanged)
        },
        content = {
            ItemListScreen(it)
        }
    )
}