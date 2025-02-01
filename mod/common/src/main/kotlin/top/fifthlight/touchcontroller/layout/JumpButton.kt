package top.fifthlight.touchcontroller.layout

import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.JumpButton
import top.fifthlight.touchcontroller.control.JumpButtonTexture
import top.fifthlight.touchcontroller.gal.KeyBindingType

private fun Context.JumpButtonTexture(size: IntSize, clicked: Boolean, texture: JumpButtonTexture) {
    withAlign(align = Align.CENTER_CENTER, size = size) {
        when (Pair(texture, clicked)) {
            Pair(JumpButtonTexture.CLASSIC, false) -> Texture(texture = Textures.GUI_JUMP_JUMP_CLASSIC)
            Pair(JumpButtonTexture.CLASSIC, true) -> Texture(
                texture = Textures.GUI_JUMP_JUMP_CLASSIC,
                color = 0xFFAAAAAAu
            )

            Pair(JumpButtonTexture.CLASSIC_FLYING, false) -> Texture(texture = Textures.GUI_JUMP_JUMP_FLYING)
            Pair(JumpButtonTexture.CLASSIC_FLYING, true) -> Texture(
                texture = Textures.GUI_JUMP_JUMP_FLYING,
                color = 0xFFAAAAAAu
            )

            Pair(JumpButtonTexture.NEW, false) -> Texture(texture = Textures.GUI_JUMP_JUMP)
            Pair(JumpButtonTexture.NEW, true) -> Texture(texture = Textures.GUI_JUMP_JUMP_ACTIVE)

            Pair(JumpButtonTexture.NEW_HORSE, false) -> Texture(texture = Textures.GUI_JUMP_JUMP_HORSE)
            Pair(JumpButtonTexture.NEW_HORSE, true) -> Texture(texture = Textures.GUI_JUMP_JUMP_HORSE_ACTIVE)
        }
    }
}

fun Context.DPadJumpButton(
    size: IntSize = this.size,
    texture: JumpButtonTexture = JumpButtonTexture.CLASSIC,
): ButtonResult = SwipeButton(id = "jump") { clicked ->
    JumpButtonTexture(size, clicked, texture)
}

fun Context.JumpButton(config: JumpButton) {
    KeyMappingButton(id = "jump", keyType = KeyBindingType.JUMP) { clicked ->
        JumpButtonTexture(config.size(), clicked, config.texture)
    }
}
