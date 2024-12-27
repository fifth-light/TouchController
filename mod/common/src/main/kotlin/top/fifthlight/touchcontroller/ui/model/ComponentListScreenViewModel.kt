package top.fifthlight.touchcontroller.ui.model

import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import top.fifthlight.combine.data.DataComponentType
import top.fifthlight.combine.screen.ViewModel
import top.fifthlight.combine.util.CloseHandler
import top.fifthlight.touchcontroller.ui.state.ComponentListScreenState

class ComponentListScreenViewModel(
    scope: CoroutineScope,
    initialList: PersistentList<DataComponentType>,
    private val onListChanged: (PersistentList<DataComponentType>) -> Unit,
) : ViewModel(scope) {
    private val _uiState = MutableStateFlow(
        ComponentListScreenState(
            list = initialList
        )
    )
    val uiState = _uiState.asStateFlow()

    fun update(list: PersistentList<DataComponentType>) {
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