package top.fifthlight.touchcontroller.control

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.width
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.Spacer
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.combine.widget.ui.IntSlider
import top.fifthlight.combine.widget.ui.Slider
import top.fifthlight.combine.widget.ui.Switch
import top.fifthlight.touchcontroller.annoations.DontTranslate

@Immutable
class BooleanProperty<Config : ControllerWidget>(
    private val getValue: (Config) -> Boolean,
    private val setValue: (Config, Boolean) -> Config,
    private val message: Text
) : ControllerWidget.Property<Config, Boolean>, KoinComponent {
    @Composable
    override fun controller(modifier: Modifier, config: Config, onConfigChanged: (Config) -> Unit) {
        Row(modifier) {
            Text(message)
            Spacer(modifier.weight(1f))
            Switch(
                checked = getValue(config),
                onChanged = {
                    onConfigChanged(setValue(config, it))
                }
            )
        }
    }
}

@Immutable
class EnumProperty<Config : ControllerWidget, T>(
    private val getValue: (Config) -> T,
    private val setValue: (Config, T) -> Config,
    private val items: List<Pair<T, Text>>,
) : ControllerWidget.Property<Config, T>, KoinComponent {
    private val textFactory: TextFactory by inject()

    private fun getItemText(item: T): Text =
        items.firstOrNull { it.first == item }?.second ?: @DontTranslate textFactory.literal(item.toString())

    @Composable
    override fun controller(modifier: Modifier, config: Config, onConfigChanged: (Config) -> Unit) {
        Button(
            modifier = modifier,
            onClick = {
                if (items.isEmpty()) {
                    return@Button
                }
                val current = getValue(config)
                val index = (items.indexOfFirst { it.first == current } + 1) % items.size
                onConfigChanged(setValue(config, items[index].first))
            }
        ) {
            Text(getItemText(getValue(config)), shadow = true)
        }
    }
}

@Immutable
class FloatProperty<Config : ControllerWidget>(
    private val getValue: (Config) -> Float,
    private val setValue: (Config, Float) -> Config,
    private val range: ClosedFloatingPointRange<Float> = 0f..1f,
    private val messageFormatter: (Float) -> Text,
) : ControllerWidget.Property<Config, Float> {

    @Composable
    override fun controller(modifier: Modifier, config: Config, onConfigChanged: (Config) -> Unit) {
        Row(modifier) {
            val value = getValue(config)
            Text(messageFormatter(value))
            Spacer(modifier.width(16))
            Slider(
                modifier = modifier.weight(1f),
                value = value,
                range = range,
                onValueChanged = {
                    onConfigChanged(setValue(config, it))
                }
            )
        }
    }
}

@Immutable
class IntProperty<Config : ControllerWidget>(
    private val getValue: (Config) -> Int,
    private val setValue: (Config, Int) -> Config,
    private val range: IntRange,
    private val messageFormatter: (Int) -> Text,
) : ControllerWidget.Property<Config, Int> {

    @Composable
    override fun controller(modifier: Modifier, config: Config, onConfigChanged: (Config) -> Unit) {
        Row(modifier) {
            val value = getValue(config)
            Text(messageFormatter(value))
            Spacer(modifier.width(16))
            IntSlider(
                modifier = modifier.weight(1f),
                value = value,
                range = range,
                onValueChanged = {
                    onConfigChanged(setValue(config, it))
                }
            )
        }
    }
}