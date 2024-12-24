package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.Composable
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.sound.LocalSoundManager
import top.fifthlight.combine.sound.SoundKind
import top.fifthlight.combine.widget.base.Canvas
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize

@Composable
fun Switch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onChanged: (Boolean) -> Unit,
    clickSound: Boolean = true,
) {
    val soundManager = LocalSoundManager.current
    Canvas(
        modifier = Modifier
            .clickable {
                if (clickSound) {
                    soundManager.play(SoundKind.BUTTON_PRESS, 1f)
                }
                onChanged(!checked)
            }
            .then(modifier),
        width = 30,
        height = 16,
    ) {
        with(canvas) {
            fillRect(
                offset = IntOffset(4, 2),
                size = IntSize(22, 12),
                color = Colors.BLACK
            )
            fillRect(
                offset = IntOffset(5, 3),
                size = IntSize(20, 10),
                color = Colors.GRAY
            )
            if (checked) {
                fillRect(
                    offset = IntOffset(0, 0),
                    size = IntSize(10, 16),
                    color = Colors.BLACK
                )
                fillRect(
                    offset = IntOffset(1, 1),
                    size = IntSize(8, 14),
                    color = Colors.WHITE
                )
            } else {
                fillRect(
                    offset = IntOffset(20, 0),
                    size = IntSize(10, 16),
                    color = Colors.BLACK
                )
                fillRect(
                    offset = IntOffset(21, 1),
                    size = IntSize(8, 14),
                    color = Colors.WHITE
                )
            }
        }
    }
}