package top.fifthlight.touchcontroller.ui.view.config.category

import androidx.compose.runtime.*
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.fillMaxHeight
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.placement.width
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.util.LocalCloseHandler
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.ui.component.config.*

data object GlobalCategory : ConfigCategory(
    title = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_TITLE,
    content = { modifier, viewModel ->
        val uiState by viewModel.uiState.collectAsState()
        val config = uiState.config
        var hoverData by remember { mutableStateOf<HoverData?>(null) }
        Row(modifier = modifier) {
            Column(
                modifier = modifier
                    .weight(1f)
                    .padding(8)
                    .verticalScroll(),
                verticalArrangement = Arrangement.spacedBy(4),
            ) {
                var globalGroupExpanded by remember { mutableStateOf(true) }
                ConfigGroup(
                    name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_TITLE),
                    expanded = globalGroupExpanded,
                    onExpandedChanged = { globalGroupExpanded = it },
                ) {
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_MOVE_TITLE),
                        value = config.disableMouseMove,
                        onValueChanged = { viewModel.updateConfig { copy(disableMouseMove = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_MOVE_TITLE,
                                    description = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_MOVE_DESCRIPTION,
                                )
                            }
                        },
                    )
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_CLICK_TITLE),
                        value = config.disableMouseClick,
                        onValueChanged = { viewModel.updateConfig { copy(disableMouseClick = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_CLICK_TITLE,
                                    description = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_CLICK_DESCRIPTION,
                                )
                            }
                        },
                    )
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_CURSOR_LOCK_TITLE),
                        value = config.disableMouseLock,
                        onValueChanged = { viewModel.updateConfig { copy(disableMouseLock = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_CURSOR_LOCK_TITLE,
                                    description = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_CURSOR_LOCK_DESCRIPTION,
                                )
                            }
                        },
                    )
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_CROSSHAIR_TITLE),
                        value = config.disableCrosshair,
                        onValueChanged = { viewModel.updateConfig { copy(disableCrosshair = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_CROSSHAIR_TITLE,
                                    description = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_CROSSHAIR_DESCRIPTION,
                                )
                            }
                        },
                    )
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_HOT_BAR_KEY_TITLE),
                        value = config.disableHotBarKey,
                        onValueChanged = { viewModel.updateConfig { copy(disableHotBarKey = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_HOT_BAR_KEY_TITLE,
                                    description = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_HOT_BAR_KEY_DESCRIPTION,
                                )
                            }
                        },
                    )
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_VIBRATION_TITLE),
                        value = config.vibration,
                        onValueChanged = { viewModel.updateConfig { copy(vibration = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_VIBRATION_TITLE,
                                    description = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_VIBRATION_DESCRIPTION,
                                )
                            }
                        },
                    )
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_QUICK_HAND_SWAP_TITLE),
                        value = config.quickHandSwap,
                        onValueChanged = { viewModel.updateConfig { copy(quickHandSwap = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_QUICK_HAND_SWAP_TITLE,
                                    description = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_REGULAR_QUICK_HAND_SWAP_DESCRIPTION,
                                )
                            }
                        },
                    )
                }

                var controlGroupExpanded by remember { mutableStateOf(true) }
                ConfigGroup(
                    name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_CONTROL_TITLE),
                    expanded = controlGroupExpanded,
                    onExpandedChanged = { controlGroupExpanded = it },
                ) {
                    FloatSliderConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_CONTROL_VIEW_MOVEMENT_SENSITIVITY_TITLE),
                        value = config.viewMovementSensitivity,
                        onValueChanged = { viewModel.updateConfig { copy(viewMovementSensitivity = it) } },
                        range = 0f..900f,
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = Texts.SCREEN_OPTIONS_CATEGORY_CONTROL_VIEW_MOVEMENT_SENSITIVITY_TITLE,
                                    description = Texts.SCREEN_OPTIONS_CATEGORY_CONTROL_VIEW_MOVEMENT_SENSITIVITY_DESCRIPTION,
                                )
                            }
                        },
                    )
                    IntSliderConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_CONTROL_VIEW_HOLD_DETECT_THRESHOLD_TITLE),
                        value = config.viewHoldDetectThreshold,
                        onValueChanged = { viewModel.updateConfig { copy(viewHoldDetectThreshold = it) } },
                        range = 0..10,
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = Texts.SCREEN_OPTIONS_CATEGORY_CONTROL_VIEW_HOLD_DETECT_THRESHOLD_TITLE,
                                    description = Texts.SCREEN_OPTIONS_CATEGORY_CONTROL_VIEW_HOLD_DETECT_THRESHOLD_DESCRIPTION,
                                )
                            }
                        },
                    )
                }

                var crosshairGroupExpanded by remember { mutableStateOf(true) }
                ConfigGroup(
                    name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_CROSSHAIR_TITLE),
                    expanded = crosshairGroupExpanded,
                    onExpandedChanged = { crosshairGroupExpanded = it },
                ) {
                    IntSliderConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_CROSSHAIR_RADIUS_TITLE),
                        value = config.crosshair.radius,
                        onValueChanged = { viewModel.updateConfig { copy(crosshair = crosshair.copy(radius = it)) } },
                        range = 16..96,
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = Texts.SCREEN_OPTIONS_CATEGORY_CROSSHAIR_RADIUS_TITLE,
                                    description = Texts.SCREEN_OPTIONS_CATEGORY_CROSSHAIR_RADIUS_DESCRIPTION,
                                )
                            }
                        },
                    )
                    IntSliderConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_CROSSHAIR_BORDER_WIDTH_TITLE),
                        value = config.crosshair.outerRadius,
                        onValueChanged = { viewModel.updateConfig { copy(crosshair = crosshair.copy(outerRadius = it)) } },
                        range = 0..8,
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = Texts.SCREEN_OPTIONS_CATEGORY_CROSSHAIR_BORDER_WIDTH_TITLE,
                                    description = Texts.SCREEN_OPTIONS_CATEGORY_CROSSHAIR_BORDER_WIDTH_DESCRIPTION,
                                )
                            }
                        },
                    )
                    FloatSliderConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_CROSSHAIR_INITIAL_PROGRESS_TITLE),
                        value = config.crosshair.initialProgress,
                        onValueChanged = { viewModel.updateConfig { copy(crosshair = crosshair.copy(initialProgress = it)) } },
                        range = 0f..1f,
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = Texts.SCREEN_OPTIONS_CATEGORY_CROSSHAIR_INITIAL_PROGRESS_TITLE,
                                    description = Texts.SCREEN_OPTIONS_CATEGORY_CROSSHAIR_INITIAL_PROGRESS_DESCRIPTION,
                                )
                            }
                        },
                    )
                }

                var debugGroupExpanded by remember { mutableStateOf(true) }
                ConfigGroup(
                    name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_DEBUG_TITLE),
                    expanded = debugGroupExpanded,
                    onExpandedChanged = { debugGroupExpanded = it },
                ) {
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_DEBUG_SHOW_POINTERS_TITLE),
                        value = config.showPointers,
                        onValueChanged = { viewModel.updateConfig { copy(showPointers = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_DEBUG_SHOW_POINTERS_TITLE,
                                    description = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_DEBUG_SHOW_POINTERS_DESCRIPTION,
                                )
                            }
                        },
                    )
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_DEBUG_ENABLE_TOUCH_EMULATION_TITLE),
                        value = config.enableTouchEmulation,
                        onValueChanged = { viewModel.updateConfig { copy(enableTouchEmulation = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_DEBUG_ENABLE_TOUCH_EMULATION_TITLE,
                                    description = Texts.SCREEN_OPTIONS_CATEGORY_GLOBAL_DEBUG_ENABLE_TOUCH_EMULATION_DESCRIPTION,
                                )
                            }
                        },
                    )
                }
            }

            val closeHandler = LocalCloseHandler.current
            DescriptionPanel(
                modifier = Modifier
                    .width(160)
                    .fillMaxHeight(),
                title = hoverData?.name?.let { Text.translatable(it) },
                description = hoverData?.description?.let { Text.translatable(it) },
                onSave = {
                    viewModel.saveAndExit(closeHandler)
                },
                onCancel = {
                    viewModel.exit(closeHandler)
                },
                onReset = {
                    viewModel.reset()
                }
            )
        }
    }
)