package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.UseButton
import top.fifthlight.touchcontroller.control.UseButtonTexture
import top.fifthlight.touchcontroller.control.UseButtonTrigger
import top.fifthlight.touchcontroller.gal.KeyBindingType

fun Context.UseButton(config: UseButton) {
    val useButtonState = keyBindingHandler.getState(KeyBindingType.USE)
    val (newPointer, clicked) = Button(id = "use") { clicked ->
        val isLockTrigger = config.trigger == UseButtonTrigger.SINGLE_CLICK_LOCK
        val locked = useButtonState.locked
        withAlign(align = Align.CENTER_CENTER, size = size) {
            when (config.texture) {
                UseButtonTexture.CLASSIC -> {
                    if (isLockTrigger && locked) {
                        if (clicked) {
                            Texture(texture = Textures.GUI_USE_USE_CLASSIC_ACTIVE, color = 0xFFAAAAAAu)
                        } else {
                            Texture(texture = Textures.GUI_USE_USE_CLASSIC_ACTIVE)
                        }
                    } else {
                        if (clicked) {
                            Texture(texture = Textures.GUI_USE_USE_CLASSIC, color = 0xFFAAAAAAu)
                        } else {
                            Texture(texture = Textures.GUI_USE_USE_CLASSIC)
                        }
                    }
                }

                UseButtonTexture.NEW -> {
                    if (clicked || locked) {
                        Texture(texture = Textures.GUI_USE_USE_ACTIVE)
                    } else {
                        Texture(texture = Textures.GUI_USE_USE)
                    }
                }
            }
        }
    }

    when (config.trigger) {
        UseButtonTrigger.SINGLE_CLICK_LOCK -> if (newPointer) {
            useButtonState.locked = !useButtonState.locked
        }

        UseButtonTrigger.HOLD -> {
            if (clicked) {
                useButtonState.clicked = true
            }
        }
    }
}