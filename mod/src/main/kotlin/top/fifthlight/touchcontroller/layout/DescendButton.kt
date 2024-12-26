package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.asset.Textures
import top.fifthlight.touchcontroller.control.DescendButton
import top.fifthlight.touchcontroller.control.DescendButtonTexture

fun Context.DescendButton(config: DescendButton) {
    val (_, clicked) = SwipeButton(id = "descend") { clicked ->
        when (Pair(config.texture, clicked)) {
            Pair(DescendButtonTexture.CLASSIC, false) -> Texture(id = Textures.DESCEND_CLASSIC)
            Pair(DescendButtonTexture.CLASSIC, true) -> Texture(id = Textures.DESCEND_CLASSIC, color = 0xFFAAAAAAu)
            Pair(DescendButtonTexture.SWIMMING, false) -> Texture(id = Textures.WATER_DESCEND)
            Pair(DescendButtonTexture.SWIMMING, true) -> Texture(id = Textures.WATER_DESCEND_ACTIVE)
            Pair(DescendButtonTexture.FLYING, false) -> Texture(id = Textures.FLYING_DESCEND)
            Pair(DescendButtonTexture.FLYING, true) -> Texture(id = Textures.FLYING_DESCEND_ACTIVE)
        }
    }
    result.sneak = result.sneak || clicked
}