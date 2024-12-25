package top.fifthlight.touchcontroller.ui.state

import kotlinx.collections.immutable.PersistentList
import net.minecraft.component.ComponentType

data class ComponentListScreenState(
    val list: PersistentList<ComponentType<*>>,
)