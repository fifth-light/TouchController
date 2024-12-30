package top.fifthlight.touchcontroller.ui.view.config.category

import androidx.compose.runtime.*
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import top.fifthlight.combine.data.LocalItemFactory
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.background
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.placement.*
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.sound.LocalSoundManager
import top.fifthlight.combine.sound.SoundKind
import top.fifthlight.combine.widget.base.Canvas
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.*
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.combine.widget.ui.DropdownMenuBox
import top.fifthlight.combine.widget.ui.EditText
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.config.LayoutLayer
import top.fifthlight.touchcontroller.config.LayoutLayerConditionKey
import top.fifthlight.touchcontroller.config.LayoutLayerConditionValue
import top.fifthlight.touchcontroller.config.TouchControllerConfig
import top.fifthlight.touchcontroller.control.ControllerWidget
import top.fifthlight.touchcontroller.gal.GameFeatures
import top.fifthlight.touchcontroller.layout.Align
import top.fifthlight.touchcontroller.layout.Context
import top.fifthlight.touchcontroller.layout.ContextResult
import top.fifthlight.touchcontroller.layout.DrawQueue
import top.fifthlight.touchcontroller.ui.state.LayoutPanelState

@Composable
private fun BoxScope.ControllerWidget(config: ControllerWidget) {
    var modifier: Modifier = Modifier.size(config.size())
    modifier = when (config.align) {
        Align.LEFT_TOP -> modifier
            .alignment(Alignment.TopLeft)
            .offset(x = config.offset.x, y = config.offset.y)

        Align.CENTER_TOP -> modifier
            .alignment(Alignment.TopCenter)
            .offset(x = config.offset.x, y = config.offset.y)

        Align.RIGHT_TOP -> modifier
            .alignment(Alignment.TopRight)
            .offset(x = -config.offset.x, y = config.offset.y)

        Align.LEFT_CENTER -> modifier
            .alignment(Alignment.CenterLeft)
            .offset(x = config.offset.x, y = config.offset.y)

        Align.CENTER_CENTER -> modifier
            .alignment(Alignment.Center)
            .offset(x = config.offset.x, y = config.offset.y)

        Align.RIGHT_CENTER -> modifier
            .alignment(Alignment.CenterRight)
            .offset(x = -config.offset.x, y = config.offset.y)

        Align.LEFT_BOTTOM -> modifier
            .alignment(Alignment.BottomLeft)
            .offset(x = config.offset.x, y = -config.offset.y)

        Align.CENTER_BOTTOM -> modifier
            .alignment(Alignment.BottomCenter)
            .offset(x = config.offset.x, y = -config.offset.y)

        Align.RIGHT_BOTTOM -> modifier
            .alignment(Alignment.BottomRight)
            .offset(x = -config.offset.x, y = -config.offset.y)
    }
    val drawQueue = DrawQueue()
    val itemFactory = LocalItemFactory.current
    val context = Context(
        windowSize = IntSize.ZERO,
        windowScaledSize = IntSize.ZERO,
        drawQueue = drawQueue,
        size = config.size(),
        screenOffset = IntOffset.ZERO,
        pointers = mutableMapOf(),
        result = ContextResult(),
        config = TouchControllerConfig.default(itemFactory),
        condition = persistentMapOf(),
    )
    config.layout(context)
    Canvas(modifier) {
        drawQueue.execute(canvas)
    }
}

@Composable
private fun WidgetsPanel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        alignment = Alignment.Center,
    ) {
        Text("TODO")
    }
}

@Composable
private fun ConditionItem(
    modifier: Modifier = Modifier,
    key: LayoutLayerConditionKey,
    value: LayoutLayerConditionValue? = null,
    onValueChanged: (LayoutLayerConditionValue?) -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val keyText = when (key) {
            LayoutLayerConditionKey.SWIMMING -> "Swimming"
            LayoutLayerConditionKey.FLYING -> "Flying"
            LayoutLayerConditionKey.SNEAKING -> "Sneaking"
            LayoutLayerConditionKey.SPRINTING -> "Sprinting"
            LayoutLayerConditionKey.ON_GROUND -> "On ground"
            LayoutLayerConditionKey.NOT_ON_GROUND -> "Not on ground"
            LayoutLayerConditionKey.USING_ITEM -> "Using item"
            LayoutLayerConditionKey.ON_MINECART -> "On minecart"
            LayoutLayerConditionKey.ON_BOAT -> "On boat"
            LayoutLayerConditionKey.ON_PIG -> "On pig"
            LayoutLayerConditionKey.ON_HORSE -> "On horse"
            LayoutLayerConditionKey.ON_DONKEY -> "On donkey"
            LayoutLayerConditionKey.ON_LLAMA -> "On llama"
            LayoutLayerConditionKey.ON_STRIDER -> "On strider"
            LayoutLayerConditionKey.RIDING -> "Riding entity"
        }
        Text(keyText)

        fun valueToText(value: LayoutLayerConditionValue?) = when (value) {
            LayoutLayerConditionValue.NEVER -> "Never"
            LayoutLayerConditionValue.WANT -> "Want"
            LayoutLayerConditionValue.REQUIRE -> "Requires"
            null -> "Ignore"
        }

        val valueText = valueToText(value)
        var expanded by remember { mutableStateOf(false) }
        DropdownMenuBox(
            modifier = Modifier.width(96),
            expanded = expanded,
            onExpandedChanged = { expanded = it },
            dropDownContent = { rect ->
                val values = listOf(
                    LayoutLayerConditionValue.NEVER,
                    LayoutLayerConditionValue.WANT,
                    LayoutLayerConditionValue.REQUIRE,
                    null,
                )
                Column {
                    for (showValue in values) {
                        Text(
                            modifier = Modifier
                                .padding(4)
                                .width(rect.size.width - 2)
                                .clickable {
                                    onValueChanged(showValue)
                                    expanded = false
                                },
                            text = valueToText(showValue),
                        )
                    }
                }
            }
        ) {
            Text(valueText)
        }
    }
}

@Composable
private fun LayersPanel(
    modifier: Modifier = Modifier,
    currentLayer: Pair<Int, LayoutLayer>? = null,
    layers: PersistentList<LayoutLayer> = persistentListOf(),
    onLayerSelected: (Int) -> Unit = {},
    onLayerChanged: (Int, LayoutLayer) -> Unit = { _, _ -> },
    onLayerRemoved: (Int, LayoutLayer) -> Unit = { _, _ -> },
    onLayerAdded: () -> Unit = {},
) {
    Row(
        modifier = modifier,
    ) {
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
                for ((index, layer) in layers.withIndex()) {
                    val soundManager = LocalSoundManager.current
                    if (currentLayer?.first == index) {
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
                                    onLayerSelected(index)
                                },
                            text = layer.name,
                        )
                    }
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onLayerAdded()
                },
            ) {
                Text("Add", shadow = true)
            }
            if (currentLayer != null) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onLayerRemoved(currentLayer.first, currentLayer.second)
                    },
                ) {
                    Text("Remove", shadow = true)
                }
            }
        }

        Spacer(
            modifier = Modifier
                .width(1)
                .fillMaxHeight()
                .background(Colors.WHITE)
        )

        if (currentLayer == null) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                alignment = Alignment.Center
            ) {
                Text("Select a layer")
            }
        } else {
            val (index, layer) = currentLayer
            Column(
                modifier = Modifier
                    .padding(8)
                    .weight(1f)
                    .verticalScroll(),
                verticalArrangement = Arrangement.spacedBy(8),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Name")
                    EditText(
                        modifier = Modifier.weight(1f),
                        value = layer.name,
                        onValueChanged = {
                            onLayerChanged(index, layer.copy(name = it))
                        }
                    )
                }


                KoinContext {
                    val gameFeatures: GameFeatures = koinInject()
                    Text("Conditions")
                    val conditionsList = listOfNotNull(
                        LayoutLayerConditionKey.SWIMMING,
                        LayoutLayerConditionKey.FLYING,
                        LayoutLayerConditionKey.SNEAKING,
                        LayoutLayerConditionKey.SPRINTING,
                        LayoutLayerConditionKey.ON_GROUND,
                        LayoutLayerConditionKey.NOT_ON_GROUND,
                        LayoutLayerConditionKey.USING_ITEM,
                        LayoutLayerConditionKey.ON_MINECART,
                        LayoutLayerConditionKey.ON_BOAT,
                        LayoutLayerConditionKey.ON_PIG,
                        LayoutLayerConditionKey.ON_HORSE,
                        LayoutLayerConditionKey.ON_DONKEY,
                        LayoutLayerConditionKey.ON_LLAMA.takeIf { gameFeatures.entity.haveLlama },
                        LayoutLayerConditionKey.ON_STRIDER.takeIf { gameFeatures.entity.haveStrider },
                        LayoutLayerConditionKey.RIDING,
                    )

                    for (condition in conditionsList) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8)
                        ) {
                            ConditionItem(
                                modifier = Modifier.fillMaxWidth(),
                                key = condition,
                                value = layer.condition[condition],
                                onValueChanged = { newValue ->
                                    onLayerChanged(
                                        index, layer.copy(
                                            condition = layer.condition.set(condition, newValue)
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetsPanel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        alignment = Alignment.Center,
    ) {
        Text("TODO")
    }
}

@Composable
private fun LayoutEditorPanel(
    modifier: Modifier = Modifier,
    layer: LayoutLayer? = null,
    onLayerChanged: (LayoutLayer) -> Unit = {},
) {
    if (layer == null) {
        Box(
            modifier = modifier,
            alignment = Alignment.Center,
        ) {
            Text("No layer selected")
        }
    } else {
        Box(modifier) {
            for (widget in layer.widgets) {
                ControllerWidget(widget)
            }
        }
    }
}

data object LayoutCategory : ConfigCategory(
    title = Texts.SCREEN_OPTIONS_CATEGORY_CUSTOM_TITLE,
    content = { modifier, viewModel ->
        val uiState by viewModel.uiState.collectAsState()

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
                Button(onClick = {
                    viewModel.toggleWidgetsPanel()
                }) {
                    Text("Add widget", shadow = true)
                }
                Button(onClick = {
                    viewModel.toggleLayersPanel()
                }) {
                    Text("Layers", shadow = true)
                }
                Button(onClick = {
                    viewModel.togglePresetsPanel()
                }) {
                    Text("Presets", shadow = true)
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(onClick = {
                    viewModel.reset()
                }) {
                    Text(Text.translatable(Texts.SCREEN_OPTIONS_RESET_TITLE), shadow = true)
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
            val currentLayer = uiState.layout.getOrNull(selectedLayer)
            when (uiState.layoutPanelState) {
                LayoutPanelState.LAYOUT -> LayoutEditorPanel(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    layer = currentLayer,
                    onLayerChanged = {
                        viewModel.updateLayer(selectedLayer, it)
                    }
                )

                LayoutPanelState.LAYERS -> LayersPanel(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    currentLayer = currentLayer?.let { Pair(selectedLayer, it) },
                    layers = uiState.layout,
                    onLayerSelected = { viewModel.setLayer(it) },
                    onLayerChanged = { index, layer ->
                        viewModel.updateLayer(index, layer)
                    },
                    onLayerAdded = {
                        viewModel.addLayer()
                    },
                    onLayerRemoved = { index, _ ->
                        viewModel.removeLayer(index)
                    },
                )

                LayoutPanelState.WIDGETS -> WidgetsPanel(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                LayoutPanelState.PRESETS -> PresetsPanel(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
)