package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.AttackButton
import top.fifthlight.touchcontroller.control.AttackButtonTexture


fun Context.AttackButton(config: AttackButton) {
    val (newClick, _, release) = Button(id = "attack") { clicked ->
        withAlign(align = Align.CENTER_CENTER, size = size) {
            when (config.texture) {
                AttackButtonTexture.CLASSIC -> {
                    if (clicked)
                        Texture(texture = Textures.GUI_ATTACK_ATTACK_CLASSIC_ACTIVE)
                    else
                        Texture(texture = Textures.GUI_ATTACK_ATTACK_CLASSIC)
                }

                AttackButtonTexture.NEW -> {
                    if (clicked)
                        Texture(texture = Textures.GUI_ATTACK_ATTACK_ACTIVE)
                    else
                        Texture(texture = Textures.GUI_ATTACK_ATTACK)
                }
            }
        }
    }
    if (newClick)
        status.attack.press()
    if (release)
        status.attack.release()
}