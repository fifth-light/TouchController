package top.fifthlight.touchcontroller.ui.view.config.category

import androidx.compose.runtime.*
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.plus
import org.koin.compose.koinInject
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.ParentDataModifierNode
import top.fifthlight.combine.modifier.drawing.background
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.drawing.innerLine
import top.fifthlight.combine.modifier.placement.*
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.modifier.pointer.consumePress
import top.fifthlight.combine.modifier.pointer.draggable
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.paint.withScale
import top.fifthlight.combine.paint.withTranslate
import top.fifthlight.combine.sound.LocalSoundManager
import top.fifthlight.combine.sound.SoundKind
import top.fifthlight.combine.widget.base.Canvas
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.*
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.combine.widget.ui.DropdownMenuBox
import top.fifthlight.combine.widget.ui.DropdownMenuIcon
import top.fifthlight.combine.widget.ui.EditText
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize
import top.fifthlight.data.Offset
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.config.LayoutLayer
import top.fifthlight.touchcontroller.config.LayoutLayerConditionKey
import top.fifthlight.touchcontroller.config.LayoutLayerConditionValue
import top.fifthlight.touchcontroller.config.TouchControllerConfig
import top.fifthlight.touchcontroller.control.*
import top.fifthlight.touchcontroller.gal.DefaultItemListProvider
import top.fifthlight.touchcontroller.gal.GameFeatures
import top.fifthlight.touchcontroller.layout.Align
import top.fifthlight.touchcontroller.layout.Context
import top.fifthlight.touchcontroller.layout.ContextResult
import top.fifthlight.touchcontroller.layout.DrawQueue
import top.fifthlight.touchcontroller.ui.state.LayoutPanelState

@Composable
private fun ControllerWidget(
    modifier: Modifier = Modifier,
    config: ControllerWidget,
) {
    val drawQueue = DrawQueue()
    val itemListProvider: DefaultItemListProvider = koinInject()
    val context = Context(
        windowSize = IntSize.ZERO,
        windowScaledSize = IntSize.ZERO,
        drawQueue = drawQueue,
        size = config.size(),
        screenOffset = IntOffset.ZERO,
        pointers = mutableMapOf(),
        result = ContextResult(),
        config = TouchControllerConfig.default(itemListProvider),
        condition = persistentMapOf(),
    )
    config.layout(context)
    Canvas(
        modifier = Modifier
            .size(config.size())
            .then(modifier)
    ) {
        drawQueue.execute(canvas)
    }
}

@Composable
private fun ScaledControllerWidget(
    modifier: Modifier = Modifier,
    config: ControllerWidget,
) {
    var entrySize by remember { mutableStateOf(IntSize.ZERO) }
    val itemListProvider: DefaultItemListProvider = koinInject()
    val (drawQueue, componentScaleFactor, offset) = remember(entrySize) {
        val queue = DrawQueue()

        val widgetSize = config.size()
        val widthFactor = if (widgetSize.width > entrySize.width) {
            entrySize.width.toFloat() / widgetSize.width.toFloat()
        } else 1f
        val heightFactor = if (widgetSize.height > entrySize.height) {
            entrySize.height.toFloat() / widgetSize.height.toFloat()
        } else 1f
        val componentScaleFactor = widthFactor.coerceAtMost(heightFactor)
        val displaySize = (widgetSize.toSize() * componentScaleFactor).toIntSize()
        val offset = (entrySize - displaySize) / 2

        val context = Context(
            windowSize = IntSize.ZERO,
            windowScaledSize = IntSize.ZERO,
            drawQueue = queue,
            size = config.size(),
            screenOffset = IntOffset.ZERO,
            pointers = mutableMapOf(),
            result = ContextResult(),
            config = TouchControllerConfig.default(itemListProvider),
            condition = persistentMapOf(),
        )
        config.layout(context)
        Triple(queue, componentScaleFactor, offset)
    }
    Canvas(
        modifier = Modifier
            .onPlaced { entrySize = it.size }
            .then(modifier),
    ) {
        canvas.withTranslate(offset) {
            canvas.withScale(componentScaleFactor) {
                drawQueue.execute(canvas)
            }
        }
    }
}

private data class WidgetItem(
    val name: Identifier,
    val config: ControllerWidget,
)

private val DEFAULT_CONFIGS = persistentListOf(
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_DPAD_NAME,
        config = DPad(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_JOYSTICK_NAME,
        config = Joystick(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_SNEAK_BUTTON_NAME,
        config = SneakButton(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_JUMP_BUTTON_NAME,
        config = JumpButton(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_PAUSE_BUTTON_NAME,
        config = PauseButton(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_CHAT_BUTTON_NAME,
        config = ChatButton(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_ASCEND_BUTTON_NAME,
        config = AscendButton(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_DESCEND_BUTTON_NAME,
        config = DescendButton(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_INVENTORY_BUTTON_NAME,
        config = InventoryButton(),
    ),
)

@Composable
private fun WidgetsPanel(
    modifier: Modifier = Modifier,
    onWidgetAdded: (ControllerWidget) -> Unit = {},
) {
    FlowRow(
        modifier = modifier
            .padding(4)
            .verticalScroll(),
    ) {
        for (config in DEFAULT_CONFIGS) {
            Column(
                modifier = Modifier.clickable { onWidgetAdded(config.config) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4),
            ) {
                ScaledControllerWidget(
                    modifier = Modifier.size(96, 72),
                    config = config.config,
                )
                Text(Text.translatable(config.name))
            }
        }
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
            LayoutLayerConditionKey.SWIMMING -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_SWIMMING_TITLE
            LayoutLayerConditionKey.FLYING -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_FLYING_TITLE
            LayoutLayerConditionKey.SNEAKING -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_SNEAKING_TITLE
            LayoutLayerConditionKey.SPRINTING -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_SPRINTING_TITLE
            LayoutLayerConditionKey.ON_GROUND -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_GROUND_TITLE
            LayoutLayerConditionKey.NOT_ON_GROUND -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_NOT_ON_GROUND_TITLE
            LayoutLayerConditionKey.USING_ITEM -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_USING_ITEM_TITLE
            LayoutLayerConditionKey.ON_MINECART -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_MINECART_TITLE
            LayoutLayerConditionKey.ON_BOAT -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_BOAT_TITLE
            LayoutLayerConditionKey.ON_PIG -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_PIG_TITLE
            LayoutLayerConditionKey.ON_HORSE -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_HORSE_TITLE
            LayoutLayerConditionKey.ON_DONKEY -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_DONKEY_TITLE
            LayoutLayerConditionKey.ON_LLAMA -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_LLAMA_TITLE
            LayoutLayerConditionKey.ON_STRIDER -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_ON_STRIDER_TITLE
            LayoutLayerConditionKey.RIDING -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_RIDING_TITLE
        }
        Text(Text.translatable(keyText))

        fun valueToText(value: LayoutLayerConditionValue?) = when (value) {
            LayoutLayerConditionValue.NEVER -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_NEVER_TITLE
            LayoutLayerConditionValue.WANT -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_WANT_TITLE
            LayoutLayerConditionValue.REQUIRE -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_REQUIRE_TITLE
            null -> Texts.SCREEN_OPTIONS_LAYER_CONDITION_IGNORE_TITLE
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
                            text = Text.translatable(valueToText(showValue)),
                        )
                    }
                }
            }
        ) {
            Text(Text.translatable(valueText))
            Spacer(modifier = Modifier.weight(1f))
            DropdownMenuIcon(expanded)
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
                Text(Text.translatable(Texts.SCREEN_OPTIONS_LAYER_ADD_TITLE), shadow = true)
            }
            if (currentLayer != null) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onLayerRemoved(currentLayer.first, currentLayer.second)
                    },
                ) {
                    Text(Text.translatable(Texts.SCREEN_OPTIONS_LAYER_REMOVE_TITLE), shadow = true)
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
                Text(Text.translatable(Texts.SCREEN_OPTIONS_LAYER_SELECT_A_LAYER_TITLE))
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
                    Text(Text.translatable(Texts.SCREEN_OPTIONS_LAYER_NAME_TITLE))
                    EditText(
                        modifier = Modifier.weight(1f),
                        value = layer.name,
                        onValueChanged = {
                            onLayerChanged(index, layer.copy(name = it))
                        }
                    )
                }

                Text(Text.translatable(Texts.SCREEN_OPTIONS_LAYER_CONDITION_TITLE))

                Text(Text.translatable(Texts.SCREEN_OPTIONS_LAYER_CONDITION_TIP))

                val gameFeatures: GameFeatures = koinInject()
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
private fun WidgetProperties(
    modifier: Modifier = Modifier,
    widget: ControllerWidget,
    onWidgetRemoved: () -> Unit = {},
    onPropertyChanged: (ControllerWidget) -> Unit = {}
) {
    Box(modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(),
            verticalArrangement = Arrangement.spacedBy(4),
        ) {
            Button(
                onClick = onWidgetRemoved,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(Text.translatable(Texts.SCREEN_OPTIONS_WIDGET_REMOVE_TITLE))
            }
            for (property in widget.properties) {
                property.controller(
                    modifier = Modifier.fillMaxWidth(),
                    config = widget,
                    onConfigChanged = onPropertyChanged
                )
            }
        }
    }
}

private data class ControllerWidgetParentData(
    val align: Align,
    val offset: IntOffset,
    val size: IntSize,
)

private data class ControllerWidgetModifierNode(
    val align: Align,
    val offset: IntOffset,
    val size: IntSize,
) : ParentDataModifierNode, Modifier.Node<ControllerWidgetModifierNode> {
    constructor(widget: ControllerWidget) : this(widget.align, widget.offset, widget.size())

    override fun modifierParentData(parentData: Any?): ControllerWidgetParentData {
        return ControllerWidgetParentData(
            align = align,
            offset = offset,
            size = size
        )
    }
}

@Composable
private fun LayoutEditorPanel(
    modifier: Modifier = Modifier,
    selectedWidgetIndex: Int = -1,
    onSelectedWidgetChanged: (Int, ControllerWidget?) -> Unit = { _, _ -> },
    layer: LayoutLayer,
    layerIndex: Int,
    onLayerChanged: (LayoutLayer) -> Unit = {},
) {
    val selectedWidget = layer.widgets.getOrNull(selectedWidgetIndex)
    Row(modifier) {
        Layout(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .consumePress {
                    onSelectedWidgetChanged(-1, null)
                },
            measurePolicy = { measurables, constraints ->
                val childConstraint = constraints.copy(minWidth = 0, minHeight = 0)
                val placeables = measurables.map { it.measure(childConstraint) }

                val width = constraints.maxWidth
                val height = constraints.maxHeight
                layout(width, height) {
                    placeables.forEachIndexed { index, placeable ->
                        val parentData = measurables[index].parentData as ControllerWidgetParentData
                        placeable.placeAt(
                            parentData.align.alignOffset(
                                windowSize = IntSize(width, height),
                                size = parentData.size,
                                offset = parentData.offset
                            )
                        )
                    }
                }
            }
        ) {
            var dragTotalOffset by remember { mutableStateOf(Offset.ZERO) }
            var widgetInitialOffset by remember { mutableStateOf(IntOffset.ZERO) }
            LaunchedEffect(selectedWidgetIndex, layerIndex, selectedWidget?.align) {
                widgetInitialOffset = layer.widgets.getOrNull(selectedWidgetIndex)?.offset ?: IntOffset.ZERO
                dragTotalOffset = Offset.ZERO
            }

            for ((index, widget) in layer.widgets.withIndex()) {
                if (selectedWidgetIndex == index) {
                    ControllerWidget(
                        modifier = Modifier
                            .then(ControllerWidgetModifierNode(widget))
                            .innerLine(Colors.WHITE)
                            .draggable { offset ->
                                dragTotalOffset += offset
                                val intOffset = dragTotalOffset.toIntOffset()
                                val appliedOffset = when (widget.align) {
                                    Align.LEFT_TOP, Align.CENTER_TOP, Align.LEFT_CENTER, Align.CENTER_CENTER -> intOffset
                                    Align.RIGHT_TOP, Align.RIGHT_CENTER -> IntOffset(-intOffset.x, intOffset.y)
                                    Align.LEFT_BOTTOM, Align.CENTER_BOTTOM -> IntOffset(intOffset.x, -intOffset.y)
                                    Align.RIGHT_BOTTOM -> -intOffset
                                }
                                val newWidget = widget.cloneBase(
                                    offset = widgetInitialOffset + appliedOffset,
                                )
                                onLayerChanged(
                                    layer.copy(
                                        widgets = layer.widgets.set(index, newWidget)
                                    )
                                )
                            },
                        config = widget
                    )
                } else {
                    ControllerWidget(
                        modifier = Modifier
                            .then(ControllerWidgetModifierNode(widget))
                            .clickable {
                                onSelectedWidgetChanged(index, widget)
                            },
                        config = widget
                    )
                }
            }
        }
        if (selectedWidget != null) {
            WidgetProperties(
                modifier = Modifier
                    .padding(4)
                    .fillMaxHeight()
                    .border(left = 1, color = Colors.WHITE)
                    .width(128),
                widget = selectedWidget,
                onWidgetRemoved = {
                    onSelectedWidgetChanged(-1, null)
                    onLayerChanged(
                        layer.copy(
                            widgets = layer.widgets.removeAt(selectedWidgetIndex),
                        )
                    )
                },
                onPropertyChanged = {
                    onLayerChanged(
                        layer.copy(
                            widgets = layer.widgets.set(selectedWidgetIndex, it)
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun LayerDropdown(
    modifier: Modifier = Modifier,
    currentLayer: LayoutLayer? = null,
    allLayers: PersistentList<LayoutLayer> = persistentListOf(),
    onLayerSelected: (Int, LayoutLayer) -> Unit = { _, _ -> },
) {
    var expanded by remember { mutableStateOf(false) }
    DropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChanged = { expanded = it },
        dropDownContent = { rect ->
            Column(Modifier.verticalScroll()) {
                for ((index, layer) in allLayers.withIndex()) {
                    Text(
                        modifier = Modifier
                            .padding(4)
                            .minWidth(rect.size.width - 2)
                            .clickable {
                                onLayerSelected(index, layer)
                                expanded = false
                            },
                        text = layer.name,
                    )
                }
            }
        }
    ) {
        if (currentLayer == null) {
            Text(text = Text.translatable(Texts.SCREEN_OPTIONS_WIDGET_NO_LAYER_SELECTED_TITLE))
        } else {
            Text(text = currentLayer.name)
        }
        DropdownMenuIcon(expanded)
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
                if (uiState.layoutPanelState == LayoutPanelState.LAYOUT) {
                    Button(onClick = {
                        viewModel.toggleWidgetsPanel()
                    }) {
                        Text(Text.translatable(Texts.SCREEN_OPTIONS_WIDGET_ADD_TITLE), shadow = true)
                    }

                    val currentLayer = uiState.layout.getOrNull(uiState.selectedLayer)
                    LayerDropdown(
                        currentLayer = currentLayer,
                        allLayers = uiState.layout,
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

                Button(onClick = {
                    viewModel.togglePresetsPanel()
                }) {
                    Text(Text.translatable(Texts.SCREEN_OPTIONS_PRESETS_TITLE), shadow = true)
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
                    layers = uiState.layout,
                    onLayerSelected = { viewModel.selectLayer(it) },
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

                LayoutPanelState.PRESETS -> PresetsPanel(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
)