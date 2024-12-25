package top.fifthlight.touchcontroller.ui.screen.config

import kotlinx.collections.immutable.PersistentList
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import top.fifthlight.combine.data.Item
import top.fifthlight.combine.platform.CombineScreen
import top.fifthlight.touchcontroller.ui.model.ItemListScreenViewModel
import top.fifthlight.touchcontroller.ui.view.config.ItemListScreen

class ItemListScreen(
    parent: Screen?,
    initialList: PersistentList<Item>,
    onListChanged: (PersistentList<Item>) -> Unit,
) : CombineScreen(Text.empty(), parent) {
    private val viewModel = ItemListScreenViewModel(this, initialList, onListChanged)

    override fun init() {
        setContent {
            ItemListScreen(viewModel)
        }
        super.init()
    }
}