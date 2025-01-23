package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.BoatButton
import top.fifthlight.touchcontroller.control.BoatButtonSide.LEFT
import top.fifthlight.touchcontroller.control.BoatButtonSide.RIGHT

fun Context.BoatButton(config: BoatButton) {
    val id = when (config.side) {
        LEFT -> "boat_left"
        RIGHT -> "boat_right"
    }
    val (_, clicked) = Button(id) { clicked ->
        if (config.classic) {
            if (clicked) {
                Texture(Textures.GUI_BOAT_BOAT_CLASSIC, color = 0xFFAAAAAAu)
            } else {
                Texture(Textures.GUI_BOAT_BOAT_CLASSIC)
            }
        } else {
            if (clicked) {
                Texture(Textures.GUI_BOAT_BOAT_ACTIVE)
            } else {
                Texture(Textures.GUI_BOAT_BOAT)
            }
        }
    }
    if (clicked) {
        when (config.side) {
            LEFT -> result.boatLeft = true
            RIGHT -> result.boatRight = true
        }
    }
}