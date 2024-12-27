package top.fifthlight.combine.platform

import androidx.compose.runtime.Composable
import net.minecraft.text.Text
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.paint.Color
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.widget.base.Text

@Composable
fun Text(
    text: Text,
    modifier: Modifier = Modifier,
    color: Color = Colors.WHITE,
    shadow: Boolean = false,
) = Text(
    text = text.string,
    modifier = modifier,
    color = color,
    shadow = shadow
)