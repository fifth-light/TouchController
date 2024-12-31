package top.fifthlight.touchcontroller.ui.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.screen.ViewModel
import top.fifthlight.combine.util.CloseHandler
import top.fifthlight.touchcontroller.config.LayoutLayer
import top.fifthlight.touchcontroller.config.TouchControllerConfig
import top.fifthlight.touchcontroller.config.TouchControllerConfigHolder
import top.fifthlight.touchcontroller.config.defaultTouchControllerLayout
import top.fifthlight.touchcontroller.gal.DefaultItemListProvider
import top.fifthlight.touchcontroller.ui.state.ConfigScreenState
import top.fifthlight.touchcontroller.ui.state.LayoutPanelState
import top.fifthlight.touchcontroller.ui.view.config.category.ConfigCategory

class ConfigScreenViewModel(
    scope: CoroutineScope,
    private val closeHandler: CloseHandler
) : ViewModel(scope), KoinComponent {
    private val configHolder: TouchControllerConfigHolder by inject()
    private val defaultItemListProvider: DefaultItemListProvider by inject()

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

    fun selectLayer(index: Int) {
        _uiState.getAndUpdate {
            require(index in it.layout.indices)
            it.copy(
                selectedLayer = index,
                selectedWidget = -1,
            )
        }
    }

    fun selectWidget(index: Int) {
        _uiState.getAndUpdate {
            if (index == -1) {
                it.copy(selectedWidget = -1)
            } else {
                val layer = it.layout.getOrNull(it.selectedLayer) ?: return@getAndUpdate it
                if (index in layer.widgets.indices) {
                    it.copy(
                        selectedWidget = index,
                    )
                } else {
                    it
                }
            }
        }
    }

    fun updateLayer(index: Int, layer: LayoutLayer) {
        _uiState.getAndUpdate {
            it.copy(
                layout = it.layout.set(index, layer)
            )
        }
    }

    fun addLayer() {
        _uiState.getAndUpdate {
            if (it.selectedLayer in it.layout.indices) {
                it.copy(
                    layout = it.layout.add(LayoutLayer())
                )
            } else {
                val newLayout = it.layout.add(LayoutLayer())
                it.copy(
                    layout = newLayout,
                    selectedLayer = newLayout.lastIndex,
                )
            }
        }
    }

    fun removeLayer(index: Int) {
        _uiState.getAndUpdate {
            if (index == it.selectedLayer) {
                it.copy(
                    layout = it.layout.removeAt(index),
                    selectedLayer = -1,
                    selectedWidget = -1,
                )
            } else {
                it.copy(
                    layout = it.layout.removeAt(index),
                )
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

    fun closePanel() {
        _uiState.getAndUpdate {
            it.copy(
                layoutPanelState = LayoutPanelState.LAYOUT
            )
        }
    }

    fun reset() {
        _uiState.getAndUpdate {
            it.copy(
                config = TouchControllerConfig.default(defaultItemListProvider),
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
        if (uiState.value.config != configHolder.config.value || uiState.value.layout != configHolder.layout.value) {
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