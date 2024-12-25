package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.*
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
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

@Composable
fun ItemGrid(
    modifier: Modifier = Modifier,
    items: PersistentList<Item>,
) {
    ItemGrid(
        modifier = modifier,
        stacks = items.map { it.toStack() }.toPersistentList()
    )
}

@JvmName("ItemStackGrid")
@Composable
fun ItemGrid(
    modifier: Modifier = Modifier,
    stacks: PersistentList<ItemStack>,
) {
    val scrollState = rememberScrollState()

    fun calculateSize(itemCount: Int, width: Int): IntSize {
        val columns = width / 16

        return if (itemCount < columns) {
            IntSize(itemCount, 1)
        } else {
            val rows = itemCount ceilDiv columns
            IntSize(columns, rows)
        }
    }

    val scrollPosition by scrollState.progress.collectAsState()
    var hoverPosition by remember { mutableStateOf<IntOffset?>(null) }
    Canvas(
        modifier = modifier
            .clickableWithOffset { }
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
            val size = if (constraints.maxWidth == Int.MAX_VALUE) {
                IntSize(stacks.size * 16, 16)
            } else {
                calculateSize(stacks.size, constraints.maxWidth) * 16
            }
            layout(size) {}
        },
    ) { node ->
        val size = calculateSize(stacks.size, node.width)
        val rowRange = scrollPosition / 16 until ((scrollPosition + node.height) ceilDiv 16)
        for (y in rowRange) {
            for (x in 0 until size.width) {
                val index = size.width * y + x
                val stack = stacks.getOrNull(index) ?: break
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