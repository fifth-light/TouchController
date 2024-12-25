package top.fifthlight.touchcontroller.ui.view.config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import net.minecraft.component.ComponentType
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.height
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.platform.toCombine
import top.fifthlight.combine.util.LocalCloseHandler
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Box
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.Spacer
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.touchcontroller.ui.component.ItemShower
import top.fifthlight.touchcontroller.ui.model.ComponentListScreenViewModel

private val itemShowerCache by lazy {
    buildMap {
        Registries.DATA_COMPONENT_TYPE.map { component ->
            val items = Registries.ITEM.filter { it.components.contains(component) }
            put(component, items.map(Item::toCombine).toPersistentList())
        }
    }.toPersistentMap()
}

@Composable
private fun ComponentRow(
    modifier: Modifier = Modifier,
    component: ComponentType<*>,
    action: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemShower(items = itemShowerCache[component])
        val name = Registries.DATA_COMPONENT_TYPE.getId(component)
        Text(text = name.toString())
        Spacer(modifier = Modifier.weight(1f))
        action()
    }
}

@Composable
fun ComponentListScreen(viewModel: ComponentListScreenViewModel) {
    Column {
        Box(
            modifier = Modifier
                .height(24)
                .fillMaxWidth()
                .border(bottom = 1, color = Colors.WHITE),
            alignment = Alignment.Center,
        ) {
            Text("Component list")
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(),
        ) {
            val uiState by viewModel.uiState.collectAsState()

            for (component in uiState.list) {
                ComponentRow(
                    modifier = Modifier
                        .padding(4)
                        .fillMaxWidth(),
                    component = component,
                ) {
                    Button(onClick = {
                        viewModel.update(uiState.list.remove(component))
                    }) {
                        Text("Remove", shadow = true)
                    }
                }
            }

            for (component in Registries.DATA_COMPONENT_TYPE) {
                ComponentRow(
                    modifier = Modifier
                        .padding(4)
                        .fillMaxWidth(),
                    component = component,
                ) {
                    Button(onClick = {
                        viewModel.update(uiState.list.add(component))
                    }) {
                        Text("Add", shadow = true)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .height(32)
                .fillMaxWidth()
                .border(top = 1, color = Colors.WHITE),
            alignment = Alignment.Center,
        ) {
            val closeHandler = LocalCloseHandler.current
            Button(
                modifier = Modifier.fillMaxWidth(.25f),
                onClick = { viewModel.done(closeHandler) }
            ) {
                Text("Done", shadow = true)
            }
        }
    }
}