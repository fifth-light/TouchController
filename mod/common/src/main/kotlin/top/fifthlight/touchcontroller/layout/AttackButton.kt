package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.AttackButton
import top.fifthlight.touchcontroller.control.AttackButtonTexture
import top.fifthlight.touchcontroller.gal.KeyBindingType

fun Context.AttackButton(config: AttackButton) {
    KeyMappingButton(id = "attack", keyType = KeyBindingType.ATTACK) { clicked ->
        withAlign(align = Align.CENTER_CENTER, size = size) {
            when (config.texture) {
                AttackButtonTexture.CLASSIC -> {
                    if (clicked) {
                        Texture(texture = Textures.GUI_ATTACK_ATTACK_CLASSIC, color = 0xFFAAAAAAu)
                    } else {
                        Texture(texture = Textures.GUI_ATTACK_ATTACK_CLASSIC)
                    }
                }

                AttackButtonTexture.NEW -> {
                    if (clicked) {
                        Texture(texture = Textures.GUI_ATTACK_ATTACK_ACTIVE)
                    } else {
                        Texture(texture = Textures.GUI_ATTACK_ATTACK)
                    }
                }
            }
        }
    }
}