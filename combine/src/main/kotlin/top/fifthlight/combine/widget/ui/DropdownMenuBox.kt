package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.*
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.modifier.Constraints
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.background
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.drawing.rotate
import top.fifthlight.combine.modifier.placement.*
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.modifier.pointer.consumePress
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.widget.base.Popup
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Box
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.RowScope
import top.fifthlight.data.IntRect
import top.fifthlight.data.IntSize

@Composable
fun DropdownMenuIcon(expanded: Boolean) {
    Text(
        text = "▶",
        modifier = Modifier.rotate(if (expanded) -90f else 90f)
    )
}

@Composable
fun DropdownMenuBox(
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onExpandedChanged: (Boolean) -> Unit,
    dropDownContent: @Composable (IntRect) -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    var anchor by remember { mutableStateOf<IntRect?>(null) }
    Row(
        modifier = Modifier
            .padding(4)
            .background(Colors.BLACK)
            .border(1, Colors.WHITE)
            .anchor {
                anchor = it
            }
            .clickable {
                onExpandedChanged(!expanded)
            }
            .then(modifier),
    ) {
        content()
    }
    val currentAnchor = anchor
    if (expanded && currentAnchor != null) {
        Popup(
            onDismissRequest = {
                onExpandedChanged(false)
            }
        ) {
            var screenSize by remember { mutableStateOf<IntSize?>(null) }
            var contentSize by remember { mutableStateOf(IntSize.ZERO) }
            val currentScreenSize = screenSize ?: IntSize.ZERO
            val top = if (currentAnchor.bottom + contentSize.height > currentScreenSize.height) {
                currentAnchor.top - contentSize.height
            } else {
                currentAnchor.bottom
            }
            val left = if (currentAnchor.left + contentSize.width > currentScreenSize.width) {
                currentScreenSize.width - contentSize.width
            } else {
                currentAnchor.left
            }
            Layout(
                modifier = Modifier
                    .fillMaxSize()
                    .onPlaced { screenSize = it.size },
                measurePolicy = { measurables, _ ->
                    val constraints = Constraints()
                    val placeables = measurables.map { it.measure(constraints) }
                    val width = placeables.maxOfOrNull { it.width } ?: 0
                    val height = placeables.maxOfOrNull { it.height } ?: 0
                    layout(width, height) {
                        placeables.forEach { it.placeAt(left, top) }
                    }
                }
            ) {
                val realScreenSize = screenSize
                if (realScreenSize != null) {
                    Box(
                        modifier = Modifier
                            .minWidth(currentAnchor.size.width - 2)
                            .maxHeight(realScreenSize.height / 2)
                            .background(Colors.BLACK)
                            .border(1, Colors.WHITE)
                            .onPlaced { contentSize = it.size }
                            .consumePress()
                    ) {
                        dropDownContent(currentAnchor)
                    }
                }
            }
        }
    }
}