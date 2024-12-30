package top.fifthlight.touchcontroller.layout

import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.SneakButton

fun Context.RawSneakButton(
    classic: Boolean = true,
    dpad: Boolean = false,
    size: IntSize = this.size
) {
    val (newPointer) = Button(id = "sneak") { clicked ->
        withAlign(align = Align.CENTER_CENTER, size = size) {
            when (Triple(dpad, classic, status.sneakLocked)) {
                Triple(false, true, false), Triple(true, true, false) -> if (clicked) {
                    Texture(texture = Textures.GUI_SNEAK_SNEAK_CLASSIC, color = 0xFFAAAAAAu)
                } else {
                    Texture(texture = Textures.GUI_SNEAK_SNEAK_CLASSIC)
                }

                Triple(false, true, true), Triple(true, true, true) -> if (clicked) {
                    Texture(texture = Textures.GUI_SNEAK_SNEAK_CLASSIC_ACTIVE, color = 0xFFAAAAAAu)
                } else {
                    Texture(texture = Textures.GUI_SNEAK_SNEAK_CLASSIC_ACTIVE)
                }

                Triple(true, false, false) -> Texture(texture = Textures.GUI_SNEAK_SNEAK_DPAD)
                Triple(true, false, true) -> Texture(texture = Textures.GUI_SNEAK_SNEAK_DPAD_ACTIVE)
                Triple(false, false, false) -> Texture(texture = Textures.GUI_SNEAK_SNEAK)
                Triple(false, false, true) -> Texture(texture = Textures.GUI_SNEAK_SNEAK_ACTIVE)
            }
        }
    }

    if (newPointer) {
        if (classic) {
            if (status.sneakLocking.click(timer.tick)) {
                status.sneakLocked = !status.sneakLocked
            }
        } else {
            status.sneakLocked = !status.sneakLocked
        }
    }
}

fun Context.SneakButton(config: SneakButton) {
    RawSneakButton(classic = config.classic, dpad = false)
}