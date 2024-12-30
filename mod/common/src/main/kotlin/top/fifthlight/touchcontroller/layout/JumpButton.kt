package top.fifthlight.touchcontroller.layout

import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.JumpButton
import top.fifthlight.touchcontroller.control.JumpButtonTexture

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
        }
    }
}

fun Context.DPadJumpButton(
    size: IntSize = this.size,
    texture: JumpButtonTexture = JumpButtonTexture.CLASSIC,
): Boolean {
    val (_, clicked) = SwipeButton(id = "jump") { clicked ->
        JumpButtonTexture(size, clicked, texture)
    }
    return clicked
}

fun Context.JumpButton(
    config: JumpButton
) {
    val (_, clicked) = Button(id = "jump") { clicked ->
        JumpButtonTexture(config.size(), clicked, config.texture)
    }
    status.jumping = status.jumping || clicked
}
