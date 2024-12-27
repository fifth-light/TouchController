package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.AscendButton
import top.fifthlight.touchcontroller.control.AscendButtonTexture

fun Context.AscendButton(config: AscendButton) {
    val (_, clicked) = SwipeButton(id = "ascend") { clicked ->
        when (Pair(config.texture, clicked)) {
            Pair(AscendButtonTexture.CLASSIC, false) -> Texture(id = Textures.GUI_ASCEND_ASCEND_CLASSIC)
            Pair(AscendButtonTexture.CLASSIC, true) -> Texture(
                id = Textures.GUI_ASCEND_ASCEND_CLASSIC,
                color = 0xFFAAAAAAu
            )

            Pair(AscendButtonTexture.SWIMMING, false) -> Texture(id = Textures.GUI_ASCEND_WATERASCEND)
            Pair(AscendButtonTexture.SWIMMING, true) -> Texture(id = Textures.GUI_ASCEND_WATERASCEND_ACTIVE)
            Pair(AscendButtonTexture.FLYING, false) -> Texture(id = Textures.GUI_ASCEND_FLYINGASCEND)
            Pair(AscendButtonTexture.FLYING, true) -> Texture(id = Textures.GUI_ASCEND_FLYINGASCEND_ACTIVE)
        }
    }
    status.jumping = status.jumping || clicked
}