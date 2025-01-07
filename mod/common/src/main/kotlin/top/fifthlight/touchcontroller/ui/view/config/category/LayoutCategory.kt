package top.fifthlight.touchcontroller.ui.view.config.category

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.collections.immutable.plus
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.height
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Box
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.Spacer
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.ui.component.config.layout.*
import top.fifthlight.touchcontroller.ui.state.LayoutPanelState

data object LayoutCategory : ConfigCategory(
    title = Texts.SCREEN_OPTIONS_CATEGORY_CUSTOM_TITLE,
    content = { modifier, viewModel ->
        val uiState by viewModel.uiState.collectAsState()
        val layers = uiState.layout.layers

        Column(modifier) {
            Row(
                modifier = Modifier
                    .padding(4)
                    .fillMaxWidth()
                    .height(32)
                    .border(bottom = 1, color = Colors.WHITE),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4),
            ) {
                if (uiState.layoutPanelState == LayoutPanelState.LAYOUT) {
                    Button(onClick = {
                        viewModel.toggleWidgetsPanel()
                    }) {
                        Text(Text.translatable(Texts.SCREEN_OPTIONS_WIDGET_ADD_TITLE), shadow = true)
                    }

                    val currentLayer = layers.getOrNull(uiState.selectedLayer)
                    LayerDropdown(
                        currentLayer = currentLayer,
                        allLayers = layers,
                        onLayerSelected = { index, _ ->
                            viewModel.selectLayer(index)
                        }
                    )

                    Button(onClick = {
                        viewModel.toggleLayersPanel()
                    }) {
                        Text(Text.translatable(Texts.SCREEN_OPTIONS_LAYER_PROPERTIES_TITLE), shadow = true)
                    }
                } else {
                    Button(onClick = {
                        viewModel.closePanel()
                    }) {
                        Text(Text.translatable(Texts.SCREEN_OPTIONS_BACK_TITLE), shadow = true)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (uiState.layoutPanelState != LayoutPanelState.PRESETS) {
                    Button(onClick = {
                        viewModel.togglePresetsPanel()
                    }) {
                        Text(Text.translatable(Texts.SCREEN_OPTIONS_PRESETS_TITLE), shadow = true)
                    }
                }
                Button(onClick = {
                    viewModel.tryExit()
                }) {
                    Text(Text.translatable(Texts.SCREEN_OPTIONS_CANCEL_TITLE), shadow = true)
                }
                Button(onClick = {
                    viewModel.saveAndExit()
                }) {
                    Text(Text.translatable(Texts.SCREEN_OPTIONS_SAVE_TITLE), shadow = true)
                }
            }
            val selectedLayer = uiState.selectedLayer
            val currentLayer = layers.getOrNull(selectedLayer)
            when (uiState.layoutPanelState) {
                LayoutPanelState.LAYOUT -> if (currentLayer != null) {
                    LayoutEditorPanel(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        layer = currentLayer,
                        layerIndex = uiState.selectedLayer,
                        onLayerChanged = {
                            viewModel.updateLayer(selectedLayer, it)
                        },
                        selectedWidgetIndex = uiState.selectedWidget,
                        onSelectedWidgetChanged = { index, _ ->
                            viewModel.selectWidget(index)
                        },
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        alignment = Alignment.Center,
                    ) {
                        Text(Text.translatable(Texts.SCREEN_OPTIONS_WIDGET_NO_LAYER_SELECTED_TITLE))
                    }
                }

                LayoutPanelState.LAYERS -> LayersPanel(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    currentLayer = currentLayer?.let { Pair(selectedLayer, it) },
                    layout = uiState.layout,
                    onLayerSelected = { viewModel.selectLayer(it) },
                    onLayerChanged = { index, layer ->
                        viewModel.updateLayer(index, layer)
                    },
                    onLayerAdded = {
                        viewModel.addLayer(it)
                    },
                    onLayerRemoved = { index, _ ->
                        viewModel.removeLayer(index)
                    },
                )

                LayoutPanelState.WIDGETS -> if (currentLayer == null) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        alignment = Alignment.Center
                    ) {
                        Text(Text.translatable(Texts.SCREEN_OPTIONS_WIDGET_SELECT_LAYER_TO_ADD_TITLE))
                    }
                } else {
                    WidgetsPanel(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        defaultOpacity = uiState.defaultOpacity,
                        onDefaultOpacityChanged = { viewModel.changeDefaultOpacity(it) },
                        onWidgetAdded = {
                            viewModel.updateLayer(
                                selectedLayer, currentLayer.copy(
                                    widgets = currentLayer.widgets + it
                                )
                            )
                            viewModel.closePanel()
                        }
                    )
                }

                LayoutPanelState.PRESETS -> {
                    val currentPreset = uiState.allPresets.getOrNull(uiState.selectedPreset)
                    PresetsPanel(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        presets = uiState.allPresets,
                        currentPreset = currentPreset?.let { Pair(uiState.selectedPreset, it) },
                        currentLayer = currentLayer,
                        onPresetSelected = {
                            viewModel.selectPreset(it)
                        },
                        onPresetAdded = {
                            viewModel.addPreset(it)
                        },
                        onPresetRemoved = {
                            viewModel.removePreset(it)
                        },
                        onPresetChanged = { index, preset ->
                            viewModel.updatePreset(index, preset)
                        },
                        onPresetSaved = {
                            viewModel.savePreset()
                        },
                        onAllLayersRead = {
                            viewModel.readAllLayers(it)
                        },
                        onLayerRead = {
                            viewModel.readLayer(it)
                        },
                        onSaveCurrentLayer = {
                            viewModel.saveLayerToPreset(it)
                        }
                    )
                }
            }
        }
    }
)