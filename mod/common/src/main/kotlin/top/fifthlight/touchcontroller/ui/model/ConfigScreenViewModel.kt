package top.fifthlight.touchcontroller.ui.model

import kotlinx.collections.immutable.plus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.screen.ViewModel
import top.fifthlight.combine.util.CloseHandler
import top.fifthlight.touchcontroller.config.*
import top.fifthlight.touchcontroller.gal.DefaultItemListProvider
import top.fifthlight.touchcontroller.ui.state.ConfigScreenState
import top.fifthlight.touchcontroller.ui.state.LayoutPanelState
import top.fifthlight.touchcontroller.ui.view.config.category.ConfigCategory
import kotlin.time.Duration.Companion.milliseconds

class ConfigScreenViewModel(
    scope: CoroutineScope,
    private val closeHandler: CloseHandler
) : ViewModel(scope), KoinComponent {
    private val configHolder: GlobalConfigHolder by inject()
    private val defaultItemListProvider: DefaultItemListProvider by inject()

    private val _uiState = MutableStateFlow(
        ConfigScreenState(
            config = configHolder.config.value,
            layout = configHolder.layout.value,
            presets = configHolder.presets.value,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        @OptIn(FlowPreview::class)
        scope.launch {
            _uiState
                .map { it.presets }
                .debounce(500.milliseconds)
                .distinctUntilChanged()
                .collectLatest {
                    configHolder.savePreset(it)
                }
        }
    }

    fun changeDefaultOpacity(opacity: Float) {
        _uiState.getAndUpdate {
            it.copy(
                defaultOpacity = opacity
            )
        }
    }

    fun selectCategory(category: ConfigCategory) {
        _uiState.getAndUpdate {
            it.copy(
                selectedCategory = category
            )
        }
    }

    fun updateConfig(update: GlobalConfig.() -> GlobalConfig) {
        _uiState.getAndUpdate {
            it.copy(
                config = update(it.config)
            )
        }
    }

    fun selectLayer(index: Int) {
        _uiState.getAndUpdate {
            require(index in it.layout.layers.indices)
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
                val layer = it.layout.layers.getOrNull(it.selectedLayer) ?: return@getAndUpdate it
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
                layout = ControllerLayout(it.layout.layers.set(index, layer))
            )
        }
    }

    fun addLayer(layer: LayoutLayer) {
        _uiState.getAndUpdate {
            if (it.selectedLayer in it.layout.layers.indices) {
                it.copy(
                    layout = ControllerLayout(it.layout.layers.add(layer))
                )
            } else {
                val newLayout = it.layout.layers.add(layer)
                it.copy(
                    layout = ControllerLayout(newLayout),
                    selectedLayer = newLayout.lastIndex,
                )
            }
        }
    }

    fun removeLayer(index: Int) {
        _uiState.getAndUpdate {
            if (index == it.selectedLayer) {
                it.copy(
                    layout = ControllerLayout(it.layout.layers.removeAt(index)),
                    selectedLayer = -1,
                    selectedWidget = -1,
                )
            } else {
                it.copy(
                    layout = ControllerLayout(it.layout.layers.removeAt(index)),
                )
            }
        }
    }

    fun selectPreset(index: Int) {
        _uiState.getAndUpdate {
            require(index in 0..<it.presets.presets.size + defaultPresets.presets.size)
            it.copy(
                selectedPreset = index,
            )
        }
    }

    fun addPreset(preset: LayoutPreset) {
        _uiState.getAndUpdate {
            it.copy(
                presets = LayoutPresets(
                    it.presets.presets + preset,
                )
            )
        }
    }

    fun savePreset() {
        _uiState.getAndUpdate {
            val currentPreset = LayoutPreset(
                name = "Saved preset",
                layout = it.layout,
            )
            it.copy(
                presets = LayoutPresets(
                    it.presets.presets + currentPreset,
                )
            )
        }
    }

    fun removePreset(index: Int) {
        val defaultPresetsSize = defaultPresets.presets.size
        val presetIndex = index - defaultPresetsSize
        _uiState.getAndUpdate {
            val preset = it.presets.presets.getOrNull(presetIndex) ?: return@getAndUpdate it
            if (preset.default) {
                return@getAndUpdate it
            }
            it.copy(
                presets = LayoutPresets(
                    it.presets.presets.removeAt(presetIndex),
                )
            )
        }
    }

    fun updatePreset(index: Int, preset: LayoutPreset) {
        val defaultPresetsSize = defaultPresets.presets.size
        val presetIndex = index - defaultPresetsSize
        _uiState.getAndUpdate {
            it.copy(
                presets = LayoutPresets(
                    it.presets.presets.set(presetIndex, preset)
                )
            )
        }
    }

    fun readAllLayers(preset: LayoutPreset) {
        _uiState.getAndUpdate {
            it.copy(
                layout = preset.layout,
            )
        }
    }

    fun readLayer(layer: LayoutLayer) {
        _uiState.getAndUpdate {
            it.copy(
                layout = ControllerLayout(
                    it.layout.layers + layer,
                )
            )
        }
    }

    fun saveLayerToPreset(layer: LayoutLayer) {
        val defaultPresetsSize = defaultPresets.presets.size
        _uiState.getAndUpdate {
            val presetIndex = it.selectedPreset - defaultPresetsSize
            val preset = it.presets.presets.getOrNull(presetIndex) ?: return@getAndUpdate it
            val newPreset = preset.copy(
                layout = ControllerLayout(
                    layers = preset.layout.layers + layer
                )
            )
            it.copy(
                presets = LayoutPresets(
                    it.presets.presets.set(presetIndex, newPreset)
                )
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
                config = GlobalConfig.default(defaultItemListProvider),
                layout = defaultControllerLayout,
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
        configHolder.savePreset(uiState.value.presets)
        closeHandler.close()
    }

    fun saveAndExit() {
        configHolder.saveConfig(uiState.value.config)
        configHolder.saveLayout(uiState.value.layout)
        configHolder.savePreset(uiState.value.presets)
        closeHandler.close()
    }
}