package top.fifthlight.touchcontroller.ui.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.util.CloseHandler
import top.fifthlight.touchcontroller.config.LayoutLayer
import top.fifthlight.touchcontroller.config.TouchControllerConfig
import top.fifthlight.touchcontroller.config.TouchControllerConfigHolder
import top.fifthlight.touchcontroller.ui.state.ConfigScreenState
import top.fifthlight.touchcontroller.ui.state.LayoutPanelState
import top.fifthlight.touchcontroller.ui.view.config.category.ConfigCategory

class ConfigScreenViewModel(scope: CoroutineScope) : ViewModel(scope), KoinComponent {
    private val configHolder: TouchControllerConfigHolder by inject()

    private val _uiState = MutableStateFlow(
        ConfigScreenState(
            config = configHolder.config.value,
            layout = configHolder.layout.value,
            selectedLayer = 0,
        )
    )
    val uiState = _uiState.asStateFlow()

    fun selectCategory(category: ConfigCategory) {
        _uiState.getAndUpdate {
            it.copy(
                selectedCategory = category
            )
        }
    }

    fun updateConfig(update: TouchControllerConfig.() -> TouchControllerConfig) {
        _uiState.getAndUpdate {
            it.copy(
                config = update(it.config)
            )
        }
    }

    fun updateLayer(index: Int, layer: LayoutLayer) {
        _uiState.getAndUpdate {
            if (index in it.layout.indices) {
                it.copy(
                    layout = it.layout.set(index, layer)
                )
            } else {
                it
            }
        }
    }

    fun toggleLayersPanel() {
        _uiState.getAndUpdate {
            it.copy(
                layoutPanelState = if (it.layoutPanelState == LayoutPanelState.LAYERS) {
                    LayoutPanelState.LAYOUT
                } else {
                    LayoutPanelState.LAYERS
                }
            )
        }
    }

    fun toggleWidgetsPanel() {
        _uiState.getAndUpdate {
            it.copy(
                layoutPanelState = if (it.layoutPanelState == LayoutPanelState.WIDGETS) {
                    LayoutPanelState.LAYOUT
                } else {
                    LayoutPanelState.WIDGETS
                }
            )
        }
    }

    fun togglePresetsPanel() {
        _uiState.getAndUpdate {
            it.copy(
                layoutPanelState = if (it.layoutPanelState == LayoutPanelState.PRESETS) {
                    LayoutPanelState.LAYOUT
                } else {
                    LayoutPanelState.PRESETS
                }
            )
        }
    }

    fun reset() {
        _uiState.getAndUpdate {
            it.copy(
                config = configHolder.config.value,
                layout = configHolder.layout.value,
            )
        }
    }

    fun exit(closeHandler: CloseHandler) {
        closeHandler.close()
    }

    fun saveAndExit(closeHandler: CloseHandler) {
        configHolder.saveConfig(uiState.value.config)
        configHolder.saveLayout(uiState.value.layout)
        closeHandler.close()
    }
}