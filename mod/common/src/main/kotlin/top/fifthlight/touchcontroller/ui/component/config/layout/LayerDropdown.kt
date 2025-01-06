package top.fifthlight.touchcontroller.ui.component.config.layout

import androidx.compose.runtime.*
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.minWidth
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.ui.DropdownMenuBox
import top.fifthlight.combine.widget.ui.DropdownMenuIcon
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.config.LayoutLayer

@Composable
fun LayerDropdown(
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