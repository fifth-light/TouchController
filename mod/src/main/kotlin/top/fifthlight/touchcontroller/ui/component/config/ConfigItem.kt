package top.fifthlight.touchcontroller.ui.component.config

import androidx.compose.runtime.Composable
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.pointer.hoverable
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.ui.Switch

data class HoverData(
    val name: String,
    val description: String,
)

@Composable
fun SwitchConfigItem(
    modifier: Modifier,
    name: String,
    value: Boolean,
    onValueChanged: (Boolean) -> Unit,
    onHovered: (Boolean) -> Unit,
)  {
    Row(
        modifier = Modifier
            .then(modifier)
            .hoverable(onHovered = onHovered),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name)
        Switch(
            checked = value,
            onChanged = onValueChanged
        )
    }
}
