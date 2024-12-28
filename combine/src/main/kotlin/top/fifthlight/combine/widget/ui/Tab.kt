package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.Composable
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.background
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.placement.fillMaxHeight
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.height
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.paint.Color
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.widget.base.layout.Box
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.RowScope

@Composable
fun Tab(modifier: Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .padding(width = 10)
            .border(bottom = 1, color = Colors.WHITE)
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(1),
        content = content,
    )
}

@Composable
fun RowScope.TabItem(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onSelected: () -> Unit = {},
    content: @Composable (Color) -> Unit,
) {
    Box(
        modifier = Modifier
            .height(24)
            .weight(1f)
            .clickable(onClick = onSelected)
            .then(modifier),
        alignment = Alignment.BottomCenter
    ) {
        val heightModifier = if (selected) Modifier.fillMaxHeight() else Modifier.height(20)
        val borderColor = if (selected) Colors.BLACK else Colors.WHITE
        val backgroundColor = if (selected) Colors.WHITE else Colors.BLACK
        Box(
            modifier = heightModifier
                .border(left = 1, top = 1, right = 1, color = borderColor)
                .background(backgroundColor)
                .fillMaxWidth(),
            alignment = Alignment.Center
        ) {
            content(borderColor)
        }
    }
}