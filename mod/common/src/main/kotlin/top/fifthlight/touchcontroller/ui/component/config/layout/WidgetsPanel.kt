package top.fifthlight.touchcontroller.ui.component.config.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.collections.immutable.persistentListOf
import org.koin.compose.koinInject
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.placement.size
import top.fifthlight.combine.modifier.placement.width
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.FlowRow
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.ui.Slider
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.control.*
import top.fifthlight.touchcontroller.gal.GameFeatures
import kotlin.math.round

private data class WidgetItem(
    val name: Identifier,
    val config: ControllerWidget,
    val condition: (GameFeatures) -> Boolean = { true },
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
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_SPRINT_BUTTON_NAME,
        config = SprintButton()
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_BOAT_BUTTON_NAME,
        config = BoatButton(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_USE_BUTTON_NAME,
        config = UseButton(),
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_ATTACK_BUTTON_NAME,
        config = AttackButton()
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_PERSPECTIVE_SWITCH_BUTTON_NAME,
        config = PerspectiveSwitchButton()
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_SCREENSHOT_BUTTON_NAME,
        config = ScreenshotButton()
    ),
    WidgetItem(
        name = Texts.SCREEN_OPTIONS_WIDGET_PANORAMA_BUTTON_NAME,
        config = PanoramaButton(),
        condition = { it.takePanorama }
    ),
)

@Composable
fun WidgetsPanel(
    modifier: Modifier = Modifier,
    defaultOpacity: Float = .6f,
    onDefaultOpacityChanged: (Float) -> Unit = {},
    onWidgetAdded: (ControllerWidget) -> Unit = {},
) {
    Column(modifier) {
        Row(
            modifier = Modifier
                .padding(4)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4),
        ) {
            Text(
                Text.format(
                    Texts.SCREEN_OPTIONS_WIDGET_DEFAULT_OPACITY_TITLE,
                    round(defaultOpacity * 100f).toString()
                )
            )
            Slider(
                modifier = Modifier.width(128),
                value = defaultOpacity,
                onValueChanged = onDefaultOpacityChanged,
                range = 0f..1f,
            )
        }
        FlowRow(
            modifier = Modifier
                .padding(4)
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(),
        ) {
            val gameFeatures: GameFeatures = koinInject()
            val configs = remember(gameFeatures) { DEFAULT_CONFIGS.filter { it.condition(gameFeatures) } }
            for (config in configs) {
                Column(
                    modifier = Modifier.clickable {
                        val newWidget = config.config.cloneBase(opacity = defaultOpacity)
                        onWidgetAdded(newWidget)
                    },
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
}