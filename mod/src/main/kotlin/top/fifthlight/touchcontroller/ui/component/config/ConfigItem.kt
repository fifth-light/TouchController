package top.fifthlight.touchcontroller.ui.component.config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import net.minecraft.client.MinecraftClient
import net.minecraft.component.ComponentType
import net.minecraft.item.Item
import net.minecraft.item.ProjectileItem
import net.minecraft.item.RangedWeaponItem
import net.minecraft.registry.Registries
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.height
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.placement.width
import top.fifthlight.combine.modifier.pointer.hoverable
import top.fifthlight.combine.platform.LocalScreen
import top.fifthlight.combine.platform.toCombine
import top.fifthlight.combine.platform.toVanilla
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.Spacer
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.combine.widget.ui.IntSlider
import top.fifthlight.combine.widget.ui.Slider
import top.fifthlight.combine.widget.ui.Switch
import top.fifthlight.touchcontroller.config.ItemList
import top.fifthlight.touchcontroller.ui.component.ItemShower
import top.fifthlight.touchcontroller.ui.screen.config.ComponentListScreen
import top.fifthlight.touchcontroller.ui.screen.config.ItemListScreen
import top.fifthlight.combine.data.Item as CombineItem
import top.fifthlight.combine.widget.ui.Item as CombineItem

data class HoverData(
    val name: String,
    val description: String,
)

@Composable
fun SwitchConfigItem(
    modifier: Modifier,
    name: String,
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
    name: String,
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
    name: String,
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
                Text("No item")
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
                Text("and ${items.size - maxItems} items")
            }
        }
    }
}

@Composable
private fun Component(
    modifier: Modifier = Modifier,
    component: ComponentType<*>
) {
    val items = remember(component) {
        Registries.ITEM
            .filter { it.components.contains(component) }
            .mapNotNull(Item::toCombine)
            .toPersistentList()
    }
    ItemShower(
        modifier = modifier,
        items = items
    )
}

@Composable
private fun ComponentListRow(
    modifier: Modifier = Modifier,
    items: PersistentList<ComponentType<*>>,
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
                Text("No component")
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
                Text("and ${items.size - maxItems} components")
            }
        }
    }
}

private val rangedWeaponItems: PersistentList<CombineItem> by lazy {
    Registries.ITEM.filterIsInstance<RangedWeaponItem>().map(Item::toCombine).toPersistentList()
}

private val projectileItems: PersistentList<CombineItem> by lazy {
    // The cast must be safe, but the compiler can't infer that
    @Suppress("UNCHECKED_CAST")
    (Registries.ITEM.filterIsInstance<ProjectileItem>() as List<Item>).map(Item::toCombine).toPersistentList()
}

@Composable
fun ItemListConfigItem(
    modifier: Modifier = Modifier,
    name: String,
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
        val screen = LocalScreen.current

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier.padding(height = 4),
                text = name,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Whitelist")
            val list = value.whitelist.map(Item::toCombine).toPersistentList()
            ItemListRow(
                modifier = Modifier.weight(1f),
                items = list,
            )
            Button(onClick = {
                val itemScreen = ItemListScreen(screen, list) {
                    onValueChanged(value.copy(whitelist = it.map(CombineItem::toVanilla).toPersistentList()))
                }
                MinecraftClient.getInstance().setScreen(itemScreen)
            }) {
                Text(text = "Edit", shadow = true)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Blacklist")
            val list = value.blacklist.map(Item::toCombine).toPersistentList()
            ItemListRow(
                modifier = Modifier.weight(1f),
                items = list,
            )
            Button(onClick = {
                val itemScreen = ItemListScreen(screen, list) {
                    onValueChanged(value.copy(blacklist = it.map(CombineItem::toVanilla).toPersistentList()))
                }
                MinecraftClient.getInstance().setScreen(itemScreen)
            }) {
                Text(text = "Edit", shadow = true)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Subclasses")

            ItemShower(items = rangedWeaponItems)
            Switch(
                checked = value.rangedWeapon,
                onChanged = { onValueChanged(value.copy(rangedWeapon = it)) }
            )

            ItemShower(items = projectileItems)
            Switch(
                checked = value.projectile,
                onChanged = { onValueChanged(value.copy(projectile = it)) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Components")
            ComponentListRow(
                modifier = Modifier.weight(1f),
                items = value.components,
            )
            Button(onClick = {
                val itemScreen = ComponentListScreen(screen, value.components) {
                    onValueChanged(value.copy(components = it))
                }
                MinecraftClient.getInstance().setScreen(itemScreen)
            }) {
                Text(text = "Edit", shadow = true)
            }
        }
    }
}