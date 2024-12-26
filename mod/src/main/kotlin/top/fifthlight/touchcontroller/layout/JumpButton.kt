package top.fifthlight.touchcontroller.layout

import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.asset.Textures
import top.fifthlight.touchcontroller.control.JumpButton

enum class JumpButtonTexture {
    CLASSIC,
    CLASSIC_FLYING,
    NEW,
}

private fun Context.JumpButtonTexture(size: IntSize, clicked: Boolean, texture: JumpButtonTexture) {
    withAlign(align = Align.CENTER_CENTER, size = size) {
        when (Pair(texture, clicked)) {
            Pair(JumpButtonTexture.CLASSIC, false) -> Texture(id = Textures.JUMP_CLASSIC)
            Pair(JumpButtonTexture.CLASSIC, true) -> Texture(id = Textures.JUMP_CLASSIC, color = 0xFFAAAAAAu)
            Pair(JumpButtonTexture.CLASSIC_FLYING, false) -> Texture(id = Textures.JUMP_FLYING)
            Pair(JumpButtonTexture.CLASSIC_FLYING, true) -> Texture(id = Textures.JUMP_FLYING, color = 0xFFAAAAAAu)
            Pair(JumpButtonTexture.NEW, false) -> Texture(id = Textures.JUMP)
            Pair(JumpButtonTexture.NEW, true) -> Texture(id = Textures.JUMP_ACTIVE)
        }
    }
}

// TODO extract a dedicated JumpButton for embed in DPad
fun Context.RawJumpButton(
    classic: Boolean = true,
    size: IntSize = this.size,
    swipe: Boolean = false,
    enabled: Boolean = true,
    texture: JumpButtonTexture = JumpButtonTexture.CLASSIC,
): Boolean {
    val (newPointer, clicked) = if (swipe) {
        SwipeButton(id = "jump") { clicked ->
            JumpButtonTexture(size, clicked, texture)
        }
    } else {
        Button(id = "jump") { clicked ->
            JumpButtonTexture(size, clicked, texture)
        }
    }
    return if (classic && condition.flying) {
        if (newPointer && status.cancelFlying.click(timer.tick)) {
            result.cancelFlying = true
        }
        false
    } else if (!enabled) {
        if (swipe) {
            clicked
        } else {
            false
        }
    } else {
        status.jumping = status.jumping || clicked
        clicked
    }
}

fun Context.JumpButton(config: JumpButton) {
    RawJumpButton(classic = config.classic)
}