package top.fifthlight.touchcontroller.ui.component.config.layout

import androidx.compose.runtime.Composable
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.scroll.verticalScroll
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Box
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.control.ControllerWidget

@Composable
fun WidgetProperties(
    modifier: Modifier = Modifier,
    widget: ControllerWidget,
    onWidgetRemoved: () -> Unit = {},
    onWidgetCut: () -> Unit = {},
    onWidgetCopied: () -> Unit = { },
    onPropertyChanged: (ControllerWidget) -> Unit = {}
) {
    Box(modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(),
            verticalArrangement = Arrangement.spacedBy(4),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4),
            ) {
                Button(
                    onClick = onWidgetCopied,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(Text.translatable(Texts.SCREEN_OPTIONS_WIDGET_COPY_TITLE), shadow = true)
                }
                Button(
                    onClick = onWidgetCut,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(Text.translatable(Texts.SCREEN_OPTIONS_WIDGET_CUT_TITLE), shadow = true)
                }
            }
            Button(
                onClick = onWidgetRemoved,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(Text.translatable(Texts.SCREEN_OPTIONS_WIDGET_REMOVE_TITLE), shadow = true)
            }
            for (property in widget.properties) {
                property.controller(
                    modifier = Modifier.fillMaxWidth(),
                    config = widget,
                    onConfigChanged = onPropertyChanged
                )
            }
        }
    }
}