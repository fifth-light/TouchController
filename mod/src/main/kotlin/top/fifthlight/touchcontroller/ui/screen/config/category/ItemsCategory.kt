package top.fifthlight.touchcontroller.ui.screen.config.category

import androidx.compose.runtime.*
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.ItemStack
import top.fifthlight.combine.widget.ui.Item
import kotlin.math.absoluteValue

data object ItemsCategory: ConfigCategory(
    title = "Items",
    content = { modifier, viewModel ->
        var size by remember { mutableIntStateOf(0) }
        LaunchedEffect(Unit) {
            while (true) {
                withFrameNanos {
                    size += 1
                }
            }
        }
        Item(
            size = ((size % 180) - 90).absoluteValue,
            itemStack = ItemStack.of(Identifier.ofVanilla("stone"), 16)
        )
    }
)