package top.fifthlight.touchcontroller.ui.view.config

import androidx.compose.runtime.*
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList
import top.fifthlight.combine.data.Item
import top.fifthlight.combine.data.LocalItemFactory
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.height
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.util.LocalCloseHandler
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Box
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.Spacer
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.combine.widget.ui.EditText
import top.fifthlight.combine.widget.ui.Item
import top.fifthlight.combine.widget.ui.ItemGrid
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.ui.model.ItemListScreenViewModel

@Composable
private fun ItemList(
    modifier: Modifier = Modifier,
    value: PersistentList<Item> = persistentListOf(),
    onValueChanged: (PersistentList<Item>) -> Unit = {},
) {
    Column(
        modifier = modifier.verticalScroll()
    ) {
        value.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .padding(4)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Item(item = item)
                Text(text = item.name)
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    onValueChanged(value.removeAt(index))
                }) {
                    Text(Text.translatable(Texts.SCREEN_ITEMS_REMOVE_TITLE), shadow = true)
                }
            }
        }
    }
}

@Composable
fun ItemListScreen(viewModel: ItemListScreenViewModel) {
    Column {
        Box(
            modifier = Modifier
                .height(24)
                .fillMaxWidth()
                .border(bottom = 1, color = Colors.WHITE),
            alignment = Alignment.Center,
        ) {
            Text(Text.translatable(Texts.SCREEN_ITEMS_TITLE))
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            val uiState by viewModel.uiState.collectAsState()

            ItemList(
                modifier = Modifier.weight(1f),
                value = uiState.list,
                onValueChanged = {
                    viewModel.update(it)
                }
            )

            Column(modifier = Modifier.weight(1f)) {
                var searchText by remember { mutableStateOf("") }
                EditText(
                    modifier = Modifier
                        .padding(8)
                        .fillMaxWidth(),
                    placeholder = Text.translatable(Texts.SCREEN_ITEMS_FILTER_PLACEHOLDER),
                    value = searchText,
                    onValueChanged = { searchText = it }
                )

                val itemFactory = LocalItemFactory.current
                ItemGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    items = if (searchText.isEmpty()) {
                        itemFactory.allItems
                    } else {
                        itemFactory.allItems.filter {
                            it.name.string.contains(searchText, ignoreCase = true)
                        }.toPersistentList()
                    },
                    onItemClicked = {
                        if (it !in uiState.list) {
                            viewModel.update(uiState.list + it)
                        }
                    },
                )
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
                Text(Text.translatable(Texts.SCREEN_ITEMS_DONE_TITLE), shadow = true)
            }
        }
    }
}