package top.fifthlight.touchcontroller.ui.component

import androidx.compose.runtime.Composable
import kotlinx.collections.immutable.toPersistentList
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.platform.toCombine
import top.fifthlight.combine.widget.ui.ItemGrid
import top.fifthlight.combine.data.Item as CombineItem

private val allItems by lazy {
    Registries.ITEM.map(Item::toCombine).toPersistentList()
}

@Composable
fun AllItemGrid(
    modifier: Modifier = Modifier,
    onItemClicked: (CombineItem) -> Unit = {}
) {
    ItemGrid(
        modifier = modifier,
        items = allItems,
    )
}