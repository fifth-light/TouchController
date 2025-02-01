package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.AscendButton
import top.fifthlight.touchcontroller.control.AscendButtonTexture
import top.fifthlight.touchcontroller.gal.KeyBindingType

fun Context.AscendButton(config: AscendButton) {
    KeyMappingSwipeButton(id = "ascend", keyType = KeyBindingType.JUMP) { clicked ->
        when (Pair(config.texture, clicked)) {
            Pair(AscendButtonTexture.CLASSIC, false) -> Texture(texture = Textures.GUI_ASCEND_ASCEND_CLASSIC)
            Pair(AscendButtonTexture.CLASSIC, true) -> Texture(
                texture = Textures.GUI_ASCEND_ASCEND_CLASSIC,
                color = 0xFFAAAAAAu
            )

            Pair(AscendButtonTexture.SWIMMING, false) -> Texture(texture = Textures.GUI_ASCEND_ASCEND_SWIMMING)
            Pair(AscendButtonTexture.SWIMMING, true) -> Texture(texture = Textures.GUI_ASCEND_ASCEND_SWIMMING_ACTIVE)
            Pair(AscendButtonTexture.FLYING, false) -> Texture(texture = Textures.GUI_ASCEND_ASCEND_FLYING)
            Pair(AscendButtonTexture.FLYING, true) -> Texture(texture = Textures.GUI_ASCEND_ASCEND_FLYING_ACTIVE)
        }
    }
}