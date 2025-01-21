package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.*
import kotlinx.collections.immutable.PersistentList
import top.fifthlight.combine.data.Item
import top.fifthlight.combine.data.ItemStack
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.pointer.clickableWithOffset
import top.fifthlight.combine.modifier.pointer.hoverableWithOffset
import top.fifthlight.combine.modifier.scroll.rememberScrollState
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.util.ceilDiv
import top.fifthlight.combine.widget.base.Canvas
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize

@JvmName("ItemStackGrid")
@Composable
fun ItemGrid(
    modifier: Modifier = Modifier,
    stacks: PersistentList<Pair<Item, ItemStack>>,
    onStackClicked: (Item, ItemStack) -> Unit = { _, _ -> },
) {
    val scrollState = rememberScrollState()

    fun calculateSize(itemCount: Int, width: Int): IntSize {
        val columns = width / 16

        return if (itemCount < columns) {
            IntSize(itemCount, 1)
        } else if (columns <= 0) {
            IntSize(0, 0)
        } else {
            val rows = itemCount ceilDiv columns
            IntSize(columns, rows)
        }
    }

    val scrollPosition by scrollState.progress.collectAsState()
    var width by remember { mutableIntStateOf(0) }
    var hoverPosition by remember { mutableStateOf<IntOffset?>(null) }
    Canvas(
        modifier = modifier
            .clickableWithOffset { position ->
                val size = calculateSize(stacks.size, width)
                val gridPosition = position.toIntOffset() / 16
                val index = gridPosition.y * size.width + gridPosition.x
                val (item, stack) = stacks.getOrNull(index) ?: return@clickableWithOffset
                onStackClicked(item, stack)
            }
            .hoverableWithOffset { hovered, position ->
                hoverPosition = when (hovered) {
                    true -> position.toIntOffset() / 16
                    false -> null
                    null -> if (hoverPosition == null) {
                        null
                    } else {
                        position.toIntOffset() / 16
                    }
                }
            }
            .verticalScroll(scrollState),
        measurePolicy = { _, constraints ->
            width = constraints.maxWidth
            val size = if (constraints.maxWidth == Int.MAX_VALUE) {
                IntSize(stacks.size * 16, 16)
            } else {
                calculateSize(stacks.size, constraints.maxWidth) * 16
            }
            layout(size) {}
        },
    ) { node ->
        val size = calculateSize(stacks.size, node.width)
        val rowRange = scrollPosition / 16 until ((scrollPosition + scrollState.viewportHeight) ceilDiv 16)
        for (y in rowRange) {
            for (x in 0 until size.width) {
                val index = size.width * y + x
                val (_, stack) = stacks.getOrNull(index) ?: break
                with(canvas) {
                    val offset = IntOffset(x, y) * 16
                    hoverPosition?.let { position ->
                        if (position.x == x && position.y == y) {
                            fillRect(offset, IntSize(16), Colors.WHITE)
                        }
                    }
                    drawItemStack(offset = offset, stack = stack)
                }
            }
        }
    }
}