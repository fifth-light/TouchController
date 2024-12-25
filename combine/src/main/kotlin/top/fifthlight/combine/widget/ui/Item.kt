package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.Composable
import top.fifthlight.combine.data.Item
import top.fifthlight.combine.data.ItemStack
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.size
import top.fifthlight.combine.widget.base.Canvas
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize

@Composable
fun Item(
    modifier: Modifier = Modifier,
    size: Int = 16,
    item: Item?
) = Item(modifier, size, item?.toStack())

@Composable
fun Item(
    modifier: Modifier = Modifier,
    size: Int = 16,
    itemStack: ItemStack?,
) {
    Canvas(
        modifier = Modifier
            .size(size)
            .then(modifier),
    ) {
        if (itemStack != null) {
            with(canvas) {
                drawItemStack(IntOffset.ZERO, IntSize(size), itemStack)
            }
        }
    }
}