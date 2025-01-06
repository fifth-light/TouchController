package top.fifthlight.touchcontroller.ui.component.config.layout

import androidx.compose.runtime.*
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.background
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.placement.fillMaxHeight
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.placement.width
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.sound.LocalSoundManager
import top.fifthlight.combine.sound.SoundKind
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Box
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.Spacer
import top.fifthlight.combine.widget.ui.AlertDialog
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.combine.widget.ui.EditText
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.config.ControllerLayout
import top.fifthlight.touchcontroller.config.LayoutLayer
import top.fifthlight.touchcontroller.config.LayoutPreset

@Composable
fun PresetsPanel(
    modifier: Modifier = Modifier,
    presets: PersistentList<LayoutPreset> = persistentListOf(),
    currentPreset: Pair<Int, LayoutPreset>? = null,
    currentLayer: LayoutLayer? = null,
    onPresetSelected: (Int) -> Unit = {},
    onPresetAdded: (LayoutPreset) -> Unit = {},
    onPresetRemoved: (Int) -> Unit = {},
    onPresetChanged: (Int, LayoutPreset) -> Unit = { _, _ -> },
    onPresetSaved: () -> Unit = {},
    onAllLayersRead: (LayoutPreset) -> Unit = {},
    onLayerRead: (LayoutLayer) -> Unit = {},
    onSaveCurrentLayer: (LayoutLayer) -> Unit = {},
) {
    Row(modifier = modifier) {
        Column(
            modifier = Modifier
                .width(128)
                .fillMaxHeight(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll()
                    .weight(1f),
            ) {
                for ((index, preset) in presets.withIndex()) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val soundManager = LocalSoundManager.current
                        if (currentPreset?.first == index) {
                            Text(
                                modifier = Modifier
                                    .padding(8)
                                    .fillMaxWidth()
                                    .background(color = Colors.WHITE)
                                    .border(bottom = 1, color = Colors.WHITE)
                                    .clickable {
                                        soundManager.play(SoundKind.BUTTON_PRESS, 1f)
                                    },
                                text = preset.name,
                                color = Colors.BLACK
                            )
                        } else {
                            Text(
                                modifier = Modifier
                                    .padding(8)
                                    .fillMaxWidth()
                                    .border(bottom = 1, color = Colors.WHITE)
                                    .clickable {
                                        soundManager.play(SoundKind.BUTTON_PRESS, 1f)
                                        onPresetSelected(index)
                                    },
                                text = preset.name,
                            )
                        }
                    }
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onPresetAdded(LayoutPreset())
                },
            ) {
                Text(Text.translatable(Texts.SCREEN_OPTIONS_PRESET_NEW_TITLE), shadow = true)
            }
            if (currentPreset != null && !currentPreset.second.default) {
                var removeDialogShown by remember { mutableStateOf(false) }
                if (removeDialogShown) {
                    AlertDialog(
                        onDismissRequest = {
                            removeDialogShown = false
                        },
                        title = {
                            Text(Text.translatable(Texts.SCREEN_OPTIONS_PRESET_DELETE_WARNING_TITLE))
                        },
                        action = {
                            Button(onClick = {
                                onPresetRemoved(currentPreset.first)
                            }) {
                                Text(
                                    Text.translatable(Texts.SCREEN_OPTIONS_PRESET_DELETE_WARNING_YES_TITLE),
                                    shadow = true
                                )
                            }
                            Button(onClick = {
                                removeDialogShown = false
                            }) {
                                Text(
                                    Text.translatable(Texts.SCREEN_OPTIONS_PRESET_DELETE_WARNING_NO_TITLE),
                                    shadow = true
                                )
                            }
                        }
                    ) {
                        Text(Text.format(Texts.SCREEN_OPTIONS_PRESET_DELETE_WARNING_MESSAGE, currentPreset.second.name))
                    }
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        removeDialogShown = true
                    },
                ) {
                    Text(Text.translatable(Texts.SCREEN_OPTIONS_PRESET_DELETE_TITLE), shadow = true)
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onPresetSaved()
                },
            ) {
                Text(Text.translatable(Texts.SCREEN_OPTIONS_PRESET_SAVE_TITLE), shadow = true)
            }
            if (currentPreset != null) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onPresetAdded(currentPreset.second.copy(default = false))
                    },
                ) {
                    Text(Text.translatable(Texts.SCREEN_OPTIONS_PRESET_COPY_TITLE), shadow = true)
                }
            }
        }

        Spacer(
            modifier = Modifier
                .width(1)
                .fillMaxHeight()
                .background(Colors.WHITE)
        )

        if (currentPreset == null) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                alignment = Alignment.Center,
            ) {
                Text(Text.translatable(Texts.SCREEN_OPTIONS_PRESET_SELECT_PRESET_TO_EDIT_TITLE))
            }
        } else {
            val (index, preset) = currentPreset
            Column(
                modifier = Modifier
                    .padding(8)
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8),
            ) {
                if (!preset.default) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(Text.translatable(Texts.SCREEN_OPTIONS_LAYER_NAME_TITLE))
                        EditText(
                            modifier = Modifier.weight(1f),
                            value = preset.name,
                            onValueChanged = {
                                onPresetChanged(index, preset.copy(name = it))
                            }
                        )
                    }
                }

                var selectedLayerIndex by remember(index, preset, presets) { mutableStateOf(0) }
                val selectedLayer = preset.layout.layers.getOrNull(selectedLayerIndex)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll()
                        .border(size = 1, color = Colors.WHITE)
                        .weight(1f)
                ) {
                    for ((index, layer) in preset.layout.layers.withIndex()) {
                        val soundManager = LocalSoundManager.current
                        if (selectedLayerIndex == index) {
                            Text(
                                modifier = Modifier
                                    .padding(8)
                                    .fillMaxWidth()
                                    .background(color = Colors.WHITE)
                                    .border(bottom = 1, color = Colors.WHITE)
                                    .clickable {
                                        soundManager.play(SoundKind.BUTTON_PRESS, 1f)
                                    },
                                text = layer.name,
                                color = Colors.BLACK
                            )
                        } else {
                            Text(
                                modifier = Modifier
                                    .padding(8)
                                    .fillMaxWidth()
                                    .border(bottom = 1, color = Colors.WHITE)
                                    .clickable {
                                        soundManager.play(SoundKind.BUTTON_PRESS, 1f)
                                        selectedLayerIndex = index
                                    },
                                text = layer.name,
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(onClick = {
                        onAllLayersRead(preset)
                    }) {
                        Text(Text.translatable(Texts.SCREEN_OPTIONS_PRESET_READ_ALL_LAYERS_TITLE), shadow = true)
                    }
                    if (selectedLayer != null) {
                        Button(onClick = {
                            onLayerRead(selectedLayer)
                        }) {
                            Text(
                                Text.translatable(Texts.SCREEN_OPTIONS_PRESET_READ_SELECTED_LAYER_TITLE),
                                shadow = true
                            )
                        }
                        if (!preset.default) {
                            Button(onClick = {
                                onPresetChanged(
                                    index, preset.copy(
                                        layout = ControllerLayout(
                                            layers = preset.layout.layers.removeAt(selectedLayerIndex)
                                        )
                                    )
                                )
                            }) {
                                Text(
                                    Text.translatable(Texts.SCREEN_OPTIONS_PRESET_DELETE_SELECTED_LAYER_TITLE),
                                    shadow = true
                                )
                            }
                        }
                    }

                    if (!preset.default && currentLayer != null) {
                        Button(onClick = {
                            onSaveCurrentLayer(currentLayer)
                        }) {
                            Text(Text.translatable(Texts.SCREEN_OPTIONS_PRESET_SAVE_CURRENT_LAYER_TITLE), shadow = true)
                        }
                    }
                }
            }
        }
    }
}