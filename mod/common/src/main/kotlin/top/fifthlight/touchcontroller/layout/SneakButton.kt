package top.fifthlight.touchcontroller.layout

import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.SneakButton
import top.fifthlight.touchcontroller.control.SneakButtonTexture
import top.fifthlight.touchcontroller.control.SneakButtonTrigger

fun Context.RawSneakButton(
    size: IntSize = this.size,
    trigger: SneakButtonTrigger = SneakButtonTrigger.DOUBLE_CLICK_LOCK,
    texture: SneakButtonTexture = SneakButtonTexture.CLASSIC,
) {
    val (newPointer, clicked) = Button(id = "sneak") { clicked ->
        val isLockTrigger =
            trigger == SneakButtonTrigger.SINGLE_CLICK_LOCK || trigger == SneakButtonTrigger.DOUBLE_CLICK_LOCK
        val showActive = (!isLockTrigger && clicked) || (isLockTrigger && status.sneakLocked)
        withAlign(align = Align.CENTER_CENTER, size = size) {
            when (texture) {
                SneakButtonTexture.CLASSIC -> if (isLockTrigger) {
                    if (status.sneakLocked) {
                        if (clicked) {
                            Texture(texture = Textures.GUI_SNEAK_SNEAK_CLASSIC_ACTIVE, color = 0xFFAAAAAAu)
                        } else {
                            Texture(texture = Textures.GUI_SNEAK_SNEAK_CLASSIC_ACTIVE)
                        }
                    } else {
                        if (clicked) {
                            Texture(texture = Textures.GUI_SNEAK_SNEAK_CLASSIC, color = 0xFFAAAAAAu)
                        } else {
                            Texture(texture = Textures.GUI_SNEAK_SNEAK_CLASSIC)
                        }
                    }
                } else {
                    if (clicked) {
                        Texture(texture = Textures.GUI_SNEAK_SNEAK_CLASSIC, color = 0xFFAAAAAAu)
                    } else {
                        Texture(texture = Textures.GUI_SNEAK_SNEAK_CLASSIC)
                    }
                }

                SneakButtonTexture.NEW -> if (showActive) {
                    Texture(texture = Textures.GUI_SNEAK_SNEAK_ACTIVE)
                } else {
                    Texture(texture = Textures.GUI_SNEAK_SNEAK)
                }

                SneakButtonTexture.NEW_DPAD -> if (showActive) {
                    Texture(texture = Textures.GUI_SNEAK_SNEAK_DPAD_ACTIVE)
                } else {
                    Texture(texture = Textures.GUI_SNEAK_SNEAK_DPAD)
                }

                SneakButtonTexture.DISMOUNT -> if (showActive) {
                    Texture(texture = Textures.GUI_DISMOUNT_DISMOUNT_ACTIVE)
                } else {
                    Texture(texture = Textures.GUI_DISMOUNT_DISMOUNT)
                }

                SneakButtonTexture.DISMOUNT_DPAD -> if (showActive) {
                    Texture(texture = Textures.GUI_JUMP_JUMP_HORSE_ACTIVE)
                } else {
                    Texture(texture = Textures.GUI_JUMP_JUMP_HORSE)
                }
            }
        }
    }

    when (trigger) {
        SneakButtonTrigger.DOUBLE_CLICK_LOCK -> if (newPointer) {
            if (status.sneakLocking.click(timer.tick)) {
                status.sneakLocked = !status.sneakLocked
            }
        }

        SneakButtonTrigger.SINGLE_CLICK_LOCK -> if (newPointer) {
            if (status.sneakLocking.click(timer.tick)) {
                status.sneakLocked = !status.sneakLocked
            }
        }

        SneakButtonTrigger.HOLD -> {
            if (newPointer) {
                status.sneaking = true
            }
            if (clicked) {
                result.sneak = true
            }
        }

        SneakButtonTrigger.SINGLE_CLICK_TRIGGER -> if (newPointer) {
            status.sneaking = true
        }
    }
}

fun Context.SneakButton(config: SneakButton) {
    RawSneakButton(
        trigger = config.trigger,
        texture = config.texture
    )
}