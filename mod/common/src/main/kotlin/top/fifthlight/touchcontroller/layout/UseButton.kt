package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.UseButton
import top.fifthlight.touchcontroller.control.UseButtonTexture

fun Context.UseButton(config: UseButton) {
    val (_, clicked) = Button(id = "use") { clicked ->
        withAlign(align = Align.CENTER_CENTER, size = size) {
            when (config.texture) {
                UseButtonTexture.CLASSIC -> {
                    if (clicked)
                        Texture(texture = Textures.GUI_USE_USE_CLASSIC_ACTIVE)
                    else
                        Texture(texture = Textures.GUI_USE_USE_CLASSIC)
                }

                UseButtonTexture.NEW -> {
                    if (clicked)
                        Texture(texture = Textures.GUI_USE_USE_ACTIVE)
                    else
                        Texture(texture = Textures.GUI_USE_USE)
                }
            }
        }
    }
    if (clicked)
        status.itemUse.press()
    else
        status.itemUse.release()
}