package top.fifthlight.touchcontroller.control

import androidx.compose.runtime.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.placement.width
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.Spacer
import top.fifthlight.combine.widget.ui.*
import top.fifthlight.touchcontroller.annoations.DontTranslate

@Immutable
class BooleanProperty<Config : ControllerWidget>(
    private val getValue: (Config) -> Boolean,
    private val setValue: (Config, Boolean) -> Config,
    private val message: Text
) : ControllerWidget.Property<Config, Boolean>, KoinComponent {
    @Composable
    override fun controller(modifier: Modifier, config: ControllerWidget, onConfigChanged: (ControllerWidget) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        val widgetConfig = config as Config
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(message)
            Spacer(modifier.weight(1f))
            Switch(
                checked = getValue(widgetConfig),
                onChanged = {
                    onConfigChanged(setValue(widgetConfig, it))
                }
            )
        }
    }
}

@Immutable
class EnumProperty<Config : ControllerWidget, T>(
    private val getValue: (Config) -> T,
    private val setValue: (Config, T) -> Config,
    private val name: Text,
    private val items: List<Pair<T, Text>>,
) : ControllerWidget.Property<Config, T>, KoinComponent {
    private val textFactory: TextFactory by inject()

    private fun getItemText(item: T): Text =
        items.firstOrNull { it.first == item }?.second ?: @DontTranslate textFactory.literal(item.toString())

    @Composable
    override fun controller(modifier: Modifier, config: ControllerWidget, onConfigChanged: (ControllerWidget) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        val widgetConfig = config as Config
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(name)

            var expanded by remember { mutableStateOf(false) }
            DropdownMenuBox(
                modifier = Modifier.weight(1f),
                expanded = expanded,
                onExpandedChanged = { expanded = it },
                dropDownContent = { rect ->
                    Column(Modifier.verticalScroll()) {
                        for ((item, text) in items) {
                            Text(
                                modifier = Modifier
                                    .padding(4)
                                    .width(rect.size.width - 2)
                                    .clickable {
                                        expanded = false
                                        onConfigChanged(setValue(widgetConfig, item))
                                    },
                                text = text,
                            )
                        }
                    }
                }
            ) {
                Text(getItemText(getValue(widgetConfig)))
                Spacer(modifier = Modifier.weight(1f))
                DropdownMenuIcon(expanded)
            }
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
    override fun controller(modifier: Modifier, config: ControllerWidget, onConfigChanged: (ControllerWidget) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        val widgetConfig = config as Config
        Column(modifier) {
            val value = getValue(widgetConfig)
            Text(messageFormatter(value))
            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                range = range,
                onValueChanged = {
                    onConfigChanged(setValue(widgetConfig, it))
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
    override fun controller(modifier: Modifier, config: ControllerWidget, onConfigChanged: (ControllerWidget) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        val widgetConfig = config as Config
        Column(modifier) {
            val value = getValue(widgetConfig)
            Text(messageFormatter(value))
            IntSlider(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                range = range,
                onValueChanged = {
                    onConfigChanged(setValue(widgetConfig, it))
                }
            )
        }
    }
}