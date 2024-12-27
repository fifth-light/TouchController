package top.fifthlight.touchcontroller.ui.component.config

import androidx.compose.runtime.Composable
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.rotate
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Box
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.ColumnScope
import top.fifthlight.combine.widget.ui.Button

@Composable
fun ConfigGroup(
    modifier: Modifier = Modifier,
    name: String,
    expanded: Boolean = true,
    onExpandedChanged: (Boolean) -> Unit = {},
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                modifier = Modifier.alignment(Alignment.CenterLeft),
                onClick = { onExpandedChanged(!expanded) }
            ) {
                Text(
                    modifier = Modifier.rotate(if (expanded) 90f else 0f),
                    text = ">",
                )
            }
            Text(
                modifier = Modifier.alignment(Alignment.Center),
                text = name,
            )
        }

        if (expanded) {
            content()
        }
    }
}

