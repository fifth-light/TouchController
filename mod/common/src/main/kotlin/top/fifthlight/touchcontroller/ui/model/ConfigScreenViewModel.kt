package top.fifthlight.touchcontroller.ui.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.data.ItemFactory
import top.fifthlight.combine.screen.ViewModel
import top.fifthlight.combine.util.CloseHandler
import top.fifthlight.touchcontroller.config.LayoutLayer
import top.fifthlight.touchcontroller.config.TouchControllerConfig
import top.fifthlight.touchcontroller.config.TouchControllerConfigHolder
import top.fifthlight.touchcontroller.config.defaultTouchControllerLayout
import top.fifthlight.touchcontroller.ui.state.ConfigScreenState
import top.fifthlight.touchcontroller.ui.state.LayoutPanelState
import top.fifthlight.touchcontroller.ui.view.config.category.ConfigCategory

class ConfigScreenViewModel(
    scope: CoroutineScope,
    private val closeHandler: CloseHandler
) : ViewModel(scope), KoinComponent {
    private val configHolder: TouchControllerConfigHolder by inject()
    private val itemFactory: ItemFactory by inject()

    private val _uiState = MutableStateFlow(
        ConfigScreenState(
            config = configHolder.config.value,
            layout = configHolder.layout.value,
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

    fun setLayer(index: Int) {
        _uiState.getAndUpdate {
            require(index in it.layout.indices)
            it.copy(
                selectedLayer = index
            )
        }
    }

    fun updateLayer(index: Int, layer: LayoutLayer) {
        _uiState.getAndUpdate {
            it.copy(
                layout = it.layout.set(index, layer)
            )
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
                config = TouchControllerConfig.default(itemFactory),
                layout = defaultTouchControllerLayout,
            )
        }
    }

    fun dismissExitDialog() {
        _uiState.getAndUpdate {
            it.copy(
                showExitDialog = false,
            )
        }
    }

    fun tryExit() {
        if (uiState.value.config != configHolder.config.value || uiState.value.layout != uiState.value.layout) {
            _uiState.getAndUpdate {
                it.copy(
                    showExitDialog = true,
                )
            }
            return
        }
        exit()
    }

    fun exit() {
        closeHandler.close()
    }

    fun saveAndExit() {
        configHolder.saveConfig(uiState.value.config)
        configHolder.saveLayout(uiState.value.layout)
        closeHandler.close()
    }
}