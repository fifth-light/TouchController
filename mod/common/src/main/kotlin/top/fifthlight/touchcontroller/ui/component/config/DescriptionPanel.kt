package top.fifthlight.touchcontroller.ui.component.config

import androidx.compose.runtime.Composable
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.background
import top.fifthlight.combine.modifier.placement.fillMaxWidth
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.Column
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.combine.widget.base.layout.Spacer
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.touchcontroller.assets.Texts

@Composable
fun DescriptionPanel(
    modifier: Modifier = Modifier,
    title: Text? = null,
    description: Text? = null,
    onReset: () -> Unit = {},
    onCancel: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(8)
            .background(Colors.BLACK)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(4),
    ) {
        title?.let { Text(it.bold()) }
        description?.let { Text(it) }

        Spacer(modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(4)) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onReset,
            ) {
                Text(Text.translatable(Texts.SCREEN_OPTIONS_RESET_TITLE), shadow = true)
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = onCancel,
            ) {
                Text(Text.translatable(Texts.SCREEN_OPTIONS_CANCEL_TITLE), shadow = true)
            }
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSave,
        ) {
            Text(Text.translatable(Texts.SCREEN_OPTIONS_SAVE_TITLE), shadow = true)
        }
    }
}