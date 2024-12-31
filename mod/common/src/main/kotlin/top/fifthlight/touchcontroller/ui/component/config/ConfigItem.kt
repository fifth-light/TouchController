package top.fifthlight.touchcontroller.ui.component.config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.collections.immutable.PersistentList
import top.fifthlight.combine.data.*
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.height
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.placement.width
import top.fifthlight.combine.modifier.pointer.hoverable
import top.fifthlight.combine.screen.LocalScreenFactory
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.Spacer
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.combine.widget.ui.IntSlider
import top.fifthlight.combine.widget.ui.Slider
import top.fifthlight.combine.widget.ui.Switch
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.config.ItemList
import top.fifthlight.touchcontroller.ui.component.ItemShower
import top.fifthlight.touchcontroller.ui.screen.config.openComponentListScreen
import top.fifthlight.touchcontroller.ui.screen.config.openItemListScreen
import top.fifthlight.combine.data.Item as CombineItem
import top.fifthlight.combine.widget.ui.Item as CombineItem

data class HoverData(
    val name: Identifier,
    val description: Identifier,
)

@Composable
fun SwitchConfigItem(
    modifier: Modifier,
    name: Text,
    value: Boolean,
    onValueChanged: (Boolean) -> Unit,
    onHovered: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .then(modifier)
            .hoverable(onHovered = onHovered),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name)
        Switch(
            checked = value,
            onChanged = onValueChanged
        )
    }
}

@Composable
fun FloatSliderConfigItem(
    modifier: Modifier,
    name: Text,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChanged: (Float) -> Unit,
    onHovered: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .then(modifier)
            .hoverable(onHovered = onHovered),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8),
    ) {
        Text(name)
        Spacer(modifier = Modifier.width(16))
        Slider(
            modifier = Modifier.weight(1f),
            range = range,
            value = value,
            onValueChanged = onValueChanged,
        )
        Text(
            text = "%.2f".format(value),
        )
    }
}

@Composable
fun IntSliderConfigItem(
    modifier: Modifier,
    name: Text,
    value: Int,
    range: IntRange,
    onValueChanged: (Int) -> Unit,
    onHovered: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .then(modifier)
            .hoverable(onHovered = onHovered),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8),
    ) {
        Text(name)
        Spacer(modifier = Modifier.width(16))
        IntSlider(
            modifier = Modifier.weight(1f),
            range = range,
            value = value,
            onValueChanged = onValueChanged,
        )
        Text(
            text = value.toString(),
        )
    }
}

@Composable
private fun ItemListRow(
    modifier: Modifier = Modifier,
    items: PersistentList<CombineItem>,
    maxItems: Int = 5,
) {
    Row(
        modifier = Modifier
            .height(16)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4),
    ) {
        when (items.size) {
            0 -> {
                Text(Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_ITEM_EMPTY))
            }

            in 1..maxItems -> {
                for (item in items) {
                    CombineItem(itemStack = item.toStack())
                }
            }

            else -> {
                for (i in 0 until items.size.coerceAtMost(maxItems)) {
                    val item = items[i]
                    CombineItem(itemStack = item.toStack())
                }
                Text(Text.format(Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_ITEM_AND_MORE_ITEMS, items.size - maxItems))
            }
        }
    }
}

@Composable
private fun Component(
    modifier: Modifier = Modifier,
    component: DataComponentType,
) {
    val items = remember(component) {
        component.allItems
    }
    ItemShower(
        modifier = modifier,
        items = items
    )
}

@Composable
private fun ComponentListRow(
    modifier: Modifier = Modifier,
    items: PersistentList<DataComponentType>,
    maxItems: Int = 5,
) {
    Row(
        modifier = Modifier
            .height(16)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4),
    ) {
        when (items.size) {
            0 -> {
                Text(Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_COMPONENT_EMPTY))
            }

            in 1..maxItems -> {
                for (item in items) {
                    Component(component = item)
                }
            }

            else -> {
                for (i in 0 until items.size.coerceAtMost(maxItems)) {
                    val item = items[i]
                    Component(component = item)
                }
                Text(
                    Text.format(
                        Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_COMPONENT_AND_MORE_COMPONENTS,
                        items.size - maxItems
                    )
                )
            }
        }
    }
}

@Composable
fun ItemListConfigItem(
    modifier: Modifier = Modifier,
    name: Text,
    value: ItemList,
    onValueChanged: (ItemList) -> Unit,
    onHovered: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .then(modifier)
            .hoverable(onHovered = onHovered),
        verticalArrangement = Arrangement.spacedBy(4),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier.padding(height = 4),
                text = name,
            )
        }

        val screenFactory = LocalScreenFactory.current
        val textFactory = LocalTextFactory.current
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_WHITELIST_TITLE))
            ItemListRow(
                modifier = Modifier.weight(1f),
                items = value.whitelist,
            )
            Button(onClick = {
                openItemListScreen(
                    screenFactory = screenFactory,
                    textFactory = textFactory,
                    initialList = value.whitelist,
                    onListChanged = {
                        onValueChanged(value.copy(whitelist = it))
                    }
                )
            }) {
                Text(text = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_EDIT_TITLE), shadow = true)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_BLACKLIST_TITLE))
            ItemListRow(
                modifier = Modifier.weight(1f),
                items = value.blacklist,
            )
            Button(onClick = {
                openItemListScreen(
                    screenFactory = screenFactory,
                    textFactory = textFactory,
                    initialList = value.blacklist,
                    onListChanged = {
                        onValueChanged(value.copy(blacklist = it))
                    }
                )
            }) {
                Text(text = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_EDIT_TITLE), shadow = true)
            }
        }

        Text(
            modifier = Modifier.padding(bottom = 4),
            text = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_SUBCLASSES_TITLE),
        )

        val itemFactory = LocalItemFactory.current
        for (subclass in itemFactory.subclasses) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(subclass.name)
                ItemShower(items = subclass.items)
                Switch(
                    checked = value.subclasses.contains(subclass),
                    onChanged = {
                        if (it) {
                            onValueChanged(value.copy(subclasses = value.subclasses.add(subclass)))
                        } else {
                            onValueChanged(value.copy(subclasses = value.subclasses.remove(subclass)))
                        }
                    }
                )
            }
        }

        val dataComponentTypeFactory: DataComponentTypeFactory = LocalDataComponentTypeFactory.current
        if (dataComponentTypeFactory.supportDataComponents) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_COMPONENT_TITLE))
                ComponentListRow(
                    modifier = Modifier.weight(1f),
                    items = value.components,
                )
                Button(onClick = {
                    openComponentListScreen(
                        screenFactory = screenFactory,
                        textFactory = textFactory,
                        initialList = value.components,
                        onListChanged = {
                            onValueChanged(value.copy(components = it))
                        }
                    )
                }) {
                    Text(text = Text.translatable(Texts.SCREEN_OPTIONS_CATEGORY_ITEMS_EDIT_TITLE), shadow = true)
                }
            }
        }
    }
}