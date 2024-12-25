package top.fifthlight.touchcontroller.ui.model

import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import top.fifthlight.combine.data.Item
import top.fifthlight.combine.util.CloseHandler
import top.fifthlight.touchcontroller.ui.state.ItemListScreenState

class ItemListScreenViewModel(
    scope: CoroutineScope,
    initialList: PersistentList<Item>,
    private val onListChanged: (PersistentList<Item>) -> Unit,
) : ViewModel(scope) {
    private val _uiState = MutableStateFlow(
        ItemListScreenState(
            list = initialList
        )
    )
    val uiState = _uiState.asStateFlow()

    fun update(list: PersistentList<Item>) {
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