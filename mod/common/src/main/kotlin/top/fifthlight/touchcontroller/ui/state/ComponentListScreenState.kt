package top.fifthlight.touchcontroller.ui.state

import kotlinx.collections.immutable.PersistentList
import top.fifthlight.combine.data.DataComponentType

data class ComponentListScreenState(
    val list: PersistentList<DataComponentType>,
)