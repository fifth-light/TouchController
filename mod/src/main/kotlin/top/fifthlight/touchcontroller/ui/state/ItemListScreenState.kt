package top.fifthlight.touchcontroller.ui.state

import kotlinx.collections.immutable.PersistentList
import top.fifthlight.combine.data.Item

data class ItemListScreenState(
    val list: PersistentList<Item>,
)