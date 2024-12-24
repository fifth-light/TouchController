package top.fifthlight.touchcontroller.ui.component.config

import androidx.compose.runtime.Composable
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.width
import top.fifthlight.combine.modifier.pointer.hoverable
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.Spacer
import top.fifthlight.combine.widget.ui.IntSlider
import top.fifthlight.combine.widget.ui.Slider
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
) {
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

@Composable
fun FloatSliderConfigItem(
    modifier: Modifier,
    name: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChanged: (Float) -> Unit,
    onHovered: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .then(modifier)
            .hoverable(onHovered = onHovered),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8),
    ) {
        Text(name)
        Spacer(modifier = Modifier.width(16))
        Slider(
            modifier = Modifier.weight(1f),
            range = range,
            value = value,
            onValueChanged = onValueChanged,
        )
        Text(
            text = "%.2f".format(value),
        )
    }
}

@Composable
fun IntSliderConfigItem(
    modifier: Modifier,
    name: String,
    value: Int,
    range: IntRange,
    onValueChanged: (Int) -> Unit,
    onHovered: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .then(modifier)
            .hoverable(onHovered = onHovered),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8),
    ) {
        Text(name)
        Spacer(modifier = Modifier.width(16))
        IntSlider(
            modifier = Modifier.weight(1f),
            range = range,
            value = value,
            onValueChanged = onValueChanged,
        )
        Text(
            text = value.toString(),
        )
    }
}