package top.fifthlight.touchcontroller.ui.model

import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import net.minecraft.component.ComponentType
import top.fifthlight.combine.util.CloseHandler
import top.fifthlight.touchcontroller.ui.state.ComponentListScreenState

class ComponentListScreenViewModel(
    scope: CoroutineScope,
    initialList: PersistentList<ComponentType<*>>,
    private val onListChanged: (PersistentList<ComponentType<*>>) -> Unit,
) : ViewModel(scope) {
    private val _uiState = MutableStateFlow(
        ComponentListScreenState(
            list = initialList
        )
    )
    val uiState = _uiState.asStateFlow()

    fun update(list: PersistentList<ComponentType<*>>) {
        _uiState.getAndUpdate {
            it.copy(
                list = list
            )
        }
    }

    fun done(closeHandler: CloseHandler) {
        closeHandler.close()
    }

    init {
        scope.launch {
            uiState.collect {
                onListChanged(it.list)
            }
        }
    }
}