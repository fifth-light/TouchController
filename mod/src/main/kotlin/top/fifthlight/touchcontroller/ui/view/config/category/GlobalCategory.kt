package top.fifthlight.touchcontroller.ui.view.config.category

import androidx.compose.runtime.*
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
import top.fifthlight.touchcontroller.ui.component.config.*

data object GlobalCategory : ConfigCategory(
    title = "Global",
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
                    name = "Global",
                    expanded = globalGroupExpanded,
                    onExpandedChanged = { globalGroupExpanded = it },
                ) {
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = "Disable mouse movement",
                        value = config.disableMouseMove,
                        onValueChanged = { viewModel.updateConfig { copy(disableMouseMove = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = "Disable mouse movement",
                                    description = "Disable mouse movement in game. Enable this option when your system maps touch input to mouse input.",
                                )
                            }
                        },
                    )
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = "Disable mouse click",
                        value = config.disableMouseClick,
                        onValueChanged = { viewModel.updateConfig { copy(disableMouseClick = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = "Disable mouse click",
                                    description = "Disable mouse clicking in game. Enable this option when your system maps touch input to mouse input.",
                                )
                            }
                        },
                    )
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = "Disable mouse lock",
                        value = config.disableMouseLock,
                        onValueChanged = { viewModel.updateConfig { copy(disableMouseLock = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = "Disable mouse lock",
                                    description = ""
                                )
                            }
                        },
                    )
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = "Disable crosshair",
                        value = config.disableCrosshair,
                        onValueChanged = { viewModel.updateConfig { copy(disableCrosshair = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = "Disable crosshair",
                                    description = ""
                                )
                            }
                        },
                    )
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = "Disable hotbar key",
                        value = config.disableHotBarKey,
                        onValueChanged = { viewModel.updateConfig { copy(disableHotBarKey = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = "Disable hotbar key",
                                    description = ""
                                )
                            }
                        },
                    )
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = "Vibration",
                        value = config.vibration,
                        onValueChanged = { viewModel.updateConfig { copy(vibration = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = "Vibration",
                                    description = ""
                                )
                            }
                        },
                    )
                    FloatSliderConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = "View movement sensitivity",
                        value = config.viewMovementSensitivity,
                        onValueChanged = { viewModel.updateConfig { copy(viewMovementSensitivity = it) } },
                        range = 0f..900f,
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = "View movement sensitivity",
                                    description = ""
                                )
                            }
                        },
                    )
                    IntSliderConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = "View hold detect threshold",
                        value = config.viewHoldDetectThreshold,
                        onValueChanged = { viewModel.updateConfig { copy(viewHoldDetectThreshold = it) } },
                        range = 0..10,
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = "View hold detect threshold",
                                    description = ""
                                )
                            }
                        },
                    )
                }

                var crosshairGroupExpanded by remember { mutableStateOf(true) }
                ConfigGroup(
                    name = "Touch crosshair",
                    expanded = crosshairGroupExpanded,
                    onExpandedChanged = { crosshairGroupExpanded = it },
                ) {
                    IntSliderConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = "Touch crosshair size",
                        value = config.crosshair.radius,
                        onValueChanged = { viewModel.updateConfig { copy(crosshair = crosshair.copy(radius = it)) } },
                        range = 16..96,
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = "Touch crosshair size",
                                    description = ""
                                )
                            }
                        },
                    )
                    IntSliderConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = "Border width",
                        value = config.crosshair.outerRadius,
                        onValueChanged = { viewModel.updateConfig { copy(crosshair = crosshair.copy(outerRadius = it)) } },
                        range = 0..8,
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = "Border width",
                                    description = ""
                                )
                            }
                        },
                    )
                    FloatSliderConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = "Initial progress",
                        value = config.crosshair.initialProgress,
                        onValueChanged = { viewModel.updateConfig { copy(crosshair = crosshair.copy(initialProgress = it)) } },
                        range = 0f..1f,
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = "Initial progress",
                                    description = ""
                                )
                            }
                        },
                    )
                }

                var debugGroupExpanded by remember { mutableStateOf(true) }
                ConfigGroup(
                    name = "Debug",
                    expanded = debugGroupExpanded,
                    onExpandedChanged = { debugGroupExpanded = it },
                ) {
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = "Show pointers",
                        value = config.showPointers,
                        onValueChanged = { viewModel.updateConfig { copy(showPointers = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = "Show pointers",
                                    description = ""
                                )
                            }
                        },
                    )
                    SwitchConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        name = "Enable touch emulation",
                        value = config.enableTouchEmulation,
                        onValueChanged = { viewModel.updateConfig { copy(enableTouchEmulation = it) } },
                        onHovered = {
                            if (it) {
                                hoverData = HoverData(
                                    name = "Enable touch emulation",
                                    description = ""
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
                title = hoverData?.name,
                description = hoverData?.description,
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