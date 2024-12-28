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
import top.fifthlight.touchcontroller.ui.component.config.DescriptionPanel
import top.fifthlight.touchcontroller.ui.component.config.HoverData
import top.fifthlight.touchcontroller.ui.component.config.ItemListConfigItem

data object ItemsCategory : ConfigCategory(
    title = Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_TITLE,
    content = { modifier, viewModel ->
        Row(modifier = modifier) {
            var hoverData by remember { mutableStateOf<HoverData?>(null) }
            val uiState by viewModel.uiState.collectAsState()

            Column(
                modifier = modifier
                    .weight(1f)
                    .padding(8)
                    .verticalScroll(),
                verticalArrangement = Arrangement.spacedBy(4),
            ) {
                ItemListConfigItem(
                    modifier = Modifier.fillMaxWidth(),
                    name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_ITEMS_TITLE),
                    value = uiState.config.usableItems,
                    onValueChanged = { viewModel.updateConfig { copy(usableItems = it) } },
                    onHovered = {
                        if (it) {
                            hoverData = HoverData(
                                name = Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_ITEMS_TITLE,
                                description = Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_ITEMS_DESCRIPTION,
                            )
                        }
                    },
                )
                ItemListConfigItem(
                    modifier = Modifier.fillMaxWidth(),
                    name = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_ITEMS_TITLE),
                    value = uiState.config.showCrosshairItems,
                    onValueChanged = { viewModel.updateConfig { copy(showCrosshairItems = it) } },
                    onHovered = {
                        if (it) {
                            hoverData = HoverData(
                                name = Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_ITEMS_TITLE,
                                description = Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_ITEMS_DESCRIPTION,
                            )
                        }
                    },
                )
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