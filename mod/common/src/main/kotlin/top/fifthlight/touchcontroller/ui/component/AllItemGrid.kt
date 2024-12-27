package top.fifthlight.touchcontroller.ui.component

import androidx.compose.runtime.Composable
import top.fifthlight.combine.data.LocalItemFactory
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.widget.ui.ItemGrid
import top.fifthlight.combine.data.Item as CombineItem

@Composable
fun AllItemGrid(
    modifier: Modifier = Modifier,
    onItemClicked: (CombineItem) -> Unit = {}
) {
    val itemFactory = LocalItemFactory.current
    ItemGrid(
        modifier = modifier,
        items = itemFactory.allItems,
        onItemClicked = onItemClicked,
    )
}