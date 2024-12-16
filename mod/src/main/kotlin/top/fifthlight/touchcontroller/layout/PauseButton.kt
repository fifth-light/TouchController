package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.asset.Textures
import top.fifthlight.touchcontroller.control.PauseButton

fun Context.PauseButton(config: PauseButton) {
    val (newClick) = Button(id = "pause") {
        if (config.classic) {
            Texture(id = Textures.PAUSE_CLASSIC)
        } else {
            Texture(id = Textures.PAUSE)
        }
    }

    result.pause = newClick
}