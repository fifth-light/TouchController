package top.fifthlight.touchcontroller.ui.component.config.layout

import androidx.compose.runtime.Composable
import kotlinx.collections.immutable.persistentListOf
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.placement.size
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.FlowRow
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.control.*

private data class WidgetItem(
    val name: Identifier,
    val config: ControllerWidget,
)

private val DEFAULT_CONFIGS = persistentListOf(
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_DPAD_NAME,
        config = DPad(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_JOYSTICK_NAME,
        config = Joystick(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_SNEAK_BUTTON_NAME,
        config = SneakButton(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_JUMP_BUTTON_NAME,
        config = JumpButton(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_PAUSE_BUTTON_NAME,
        config = PauseButton(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_CHAT_BUTTON_NAME,
        config = ChatButton(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_ASCEND_BUTTON_NAME,
        config = AscendButton(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_DESCEND_BUTTON_NAME,
        config = DescendButton(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_INVENTORY_BUTTON_NAME,
        config = InventoryButton(),
    ),
)

@Composable
fun WidgetsPanel(
    modifier: Modifier = Modifier,
    onWidgetAdded: (ControllerWidget) -> Unit = {},
) {
    FlowRow(
        modifier = modifier
            .padding(4)
            .verticalScroll(),
    ) {
        for (config in DEFAULT_CONFIGS) {
            Column(
                modifier = Modifier.clickable { onWidgetAdded(config.config) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4),
            ) {
                ScaledControllerWidget(
                    modifier = Modifier.size(96, 72),
                    config = config.config,
                )
                Text(Text.translatable(config.name))
            }
        }
    }
}