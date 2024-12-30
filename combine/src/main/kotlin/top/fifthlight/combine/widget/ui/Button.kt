package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.*
import top.fifthlight.combine.input.MutableInteractionSource
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.guiTextureBackground
import top.fifthlight.combine.modifier.placement.minSize
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.pointer.ClickInteraction
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.paint.GuiTexture
import top.fifthlight.combine.sound.LocalSoundManager
import top.fifthlight.combine.sound.SoundKind
import top.fifthlight.combine.widget.base.layout.Box
import top.fifthlight.combine.widget.base.layout.BoxScope

@Composable
fun Button(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    clickSound: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val soundManager = LocalSoundManager.current
    val interactionSource = remember { MutableInteractionSource() }
    var texture by remember { mutableStateOf(GuiTexture.BUTTON) }
    LaunchedEffect(Unit) {
        interactionSource.interactions.collect {
            when (it) {
                ClickInteraction.Empty -> {
                    texture = GuiTexture.BUTTON
                }

                ClickInteraction.Active -> {
                    texture = GuiTexture.BUTTON_ACTIVE
                }

                ClickInteraction.Hover -> {
                    texture = GuiTexture.BUTTON_HOVER
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .padding(4)
            .minSize(48, 20)
            .clickable(interactionSource) {
                if (clickSound) {
                    soundManager.play(SoundKind.BUTTON_PRESS, 1f)
                }
                onClick()
            }
            .guiTextureBackground(texture)
            .then(modifier),
        alignment = Alignment.Center,
        content = content
    )
}