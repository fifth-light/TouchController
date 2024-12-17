package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.asset.Textures
import top.fifthlight.touchcontroller.control.DescendButton

fun Context.DescendButton(config: DescendButton) {
    if (designMode || state != HudState.NORMAL) {
        val (_, clicked) = Button(id = "descend") { clicked ->
            if (config.classic) {
                if (clicked) {
                    Texture(id = Textures.DESCEND_CLASSIC, color = 0xFFAAAAAAu)
                } else {
                    Texture(id = Textures.DESCEND_CLASSIC)
                }
            } else if (designMode) {
                Texture(id = Textures.FLYING_DESCEND)
            } else {
                when (Pair(state, clicked)) {
                    Pair(HudState.SWIMMING, false) -> Texture(id = Textures.WATER_DESCEND)
                    Pair(HudState.SWIMMING, true) -> Texture(id = Textures.WATER_DESCEND_ACTIVE)
                    Pair(HudState.FLYING, false) -> Texture(id = Textures.FLYING_DESCEND)
                    Pair(HudState.FLYING, true) -> Texture(id = Textures.FLYING_DESCEND_ACTIVE)
                }
            }
        }
        result.sneak = result.sneak || clicked
    }
}