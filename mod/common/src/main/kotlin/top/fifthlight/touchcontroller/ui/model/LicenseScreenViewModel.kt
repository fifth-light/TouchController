package top.fifthlight.touchcontroller.ui.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.fifthlight.combine.screen.ViewModel
import top.fifthlight.combine.util.CloseHandler
import top.fifthlight.touchcontroller.ui.state.LicenseScreenState

class LicenseScreenViewModel(
    scope: CoroutineScope,
    licenseText: String,
) : ViewModel(scope) {
    private val _uiState = MutableStateFlow(
        LicenseScreenState(
            licenseText = licenseText
        )
    )
    val uiState = _uiState.asStateFlow()

    fun close(handler: CloseHandler) {
        handler.close()
    }
}