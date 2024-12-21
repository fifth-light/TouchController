package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.Composable
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.guiTextureBackground
import top.fifthlight.combine.modifier.placement.minSize
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.sound.LocalSoundManager
import top.fifthlight.combine.sound.SoundKind
import top.fifthlight.combine.widget.base.layout.Box

@Composable
fun Button(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    clickSound: Boolean = false,
    content: @Composable () -> Unit = {}
) {
    val soundManager = LocalSoundManager.current
    Box(
        modifier = Modifier
            .padding(4)
            .minSize(48, 20)
            .clickable {
                if (clickSound) {
                    soundManager.play(SoundKind.BUTTON_PRESS, 1f)
                }
                onClick()
            }
            .guiTextureBackground(Identifier.ofVanilla("widget/button"))
            .then(modifier),
        alignment = Alignment.Center,
        content = content
    )
}