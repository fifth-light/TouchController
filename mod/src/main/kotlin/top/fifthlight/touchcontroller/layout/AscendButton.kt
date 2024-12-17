package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.asset.Textures
import top.fifthlight.touchcontroller.control.AscendButton

fun Context.AscendButton(config: AscendButton) {
    if (designMode || state != HudState.NORMAL) {
        val (_, clicked) = Button(id = "ascend") { clicked ->
            if (config.classic) {
                if (clicked) {
                    Texture(id = Textures.ASCEND_CLASSIC, color = 0xFFAAAAAAu)
                } else {
                    Texture(id = Textures.ASCEND_CLASSIC)
                }
            } else if (designMode) {
                Texture(id = Textures.FLYING_ASCEND)
            } else {
                when (Pair(state, clicked)) {
                    Pair(HudState.SWIMMING, false) -> Texture(id = Textures.WATER_ASCEND)
                    Pair(HudState.SWIMMING, true) -> Texture(id = Textures.WATER_ASCEND_ACTIVE)
                    Pair(HudState.FLYING, false) -> Texture(id = Textures.FLYING_ASCEND)
                    Pair(HudState.FLYING, true) -> Texture(id = Textures.FLYING_ASCEND_ACTIVE)
                }
            }
        }
        status.jumping = status.jumping || clicked
    }
}