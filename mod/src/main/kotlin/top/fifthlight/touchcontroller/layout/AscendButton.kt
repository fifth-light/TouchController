package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.asset.Textures
import top.fifthlight.touchcontroller.control.AscendButton
import top.fifthlight.touchcontroller.control.AscendButtonTexture

fun Context.AscendButton(config: AscendButton) {
    val (_, clicked) = SwipeButton(id = "ascend") { clicked ->
        when (Pair(config.texture, clicked)) {
            Pair(AscendButtonTexture.CLASSIC, false) -> Texture(id = Textures.ASCEND_CLASSIC)
            Pair(AscendButtonTexture.CLASSIC, true) -> Texture(id = Textures.ASCEND_CLASSIC, color = 0xFFAAAAAAu)
            Pair(AscendButtonTexture.SWIMMING, false) -> Texture(id = Textures.WATER_ASCEND)
            Pair(AscendButtonTexture.SWIMMING, true) -> Texture(id = Textures.WATER_ASCEND_ACTIVE)
            Pair(AscendButtonTexture.FLYING, false) -> Texture(id = Textures.FLYING_ASCEND)
            Pair(AscendButtonTexture.FLYING, true) -> Texture(id = Textures.FLYING_ASCEND_ACTIVE)
        }
    }
    status.jumping = status.jumping || clicked
}