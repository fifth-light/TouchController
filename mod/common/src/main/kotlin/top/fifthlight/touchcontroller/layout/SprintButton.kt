package top.fifthlight.touchcontroller.layout

import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.SprintButton
import top.fifthlight.touchcontroller.control.SprintButtonTexture
import top.fifthlight.touchcontroller.control.SprintButtonTexture.CLASSIC
import top.fifthlight.touchcontroller.control.SprintButtonTexture.NEW
import top.fifthlight.touchcontroller.control.SprintButtonTrigger
import top.fifthlight.touchcontroller.control.SprintButtonTrigger.HOLD
import top.fifthlight.touchcontroller.control.SprintButtonTrigger.SINGLE_CLICK_LOCK

fun Context.RawSprintButton(
    size: IntSize = this.size,
    trigger: SprintButtonTrigger = SINGLE_CLICK_LOCK,
    texture: SprintButtonTexture = CLASSIC,
) {
    val (newPointer, clicked) = Button(id = "sprint") { clicked ->
        val isLockTrigger =
            trigger == SINGLE_CLICK_LOCK
        val showActive = (!isLockTrigger && clicked) || (isLockTrigger && status.sprintLocked)
        withAlign(align = Align.CENTER_CENTER, size = size) {
            when (texture) {
                CLASSIC -> if (isLockTrigger) {
                    if (status.sprintLocked) {
                        if (clicked) {
                            Texture(texture = Textures.GUI_SPRINT_SPRINT_CLASSIC_ACTIVE, color = 0xFFAAAAAAu)
                        } else {
                            Texture(texture = Textures.GUI_SPRINT_SPRINT_CLASSIC_ACTIVE)
                        }
                    } else {
                        if (clicked) {
                            Texture(texture = Textures.GUI_SPRINT_SPRINT_CLASSIC, color = 0xFFAAAAAAu)
                        } else {
                            Texture(texture = Textures.GUI_SPRINT_SPRINT_CLASSIC)
                        }
                    }
                } else {
                    if (clicked) {
                        Texture(texture = Textures.GUI_SPRINT_SPRINT_CLASSIC, color = 0xFFAAAAAAu)
                    } else {
                        Texture(texture = Textures.GUI_SPRINT_SPRINT_CLASSIC)
                    }

                }

                NEW -> if (showActive) {
                    Texture(texture = Textures.GUI_SPRINT_SPRINT_ACTIVE)
                } else {
                    Texture(texture = Textures.GUI_SPRINT_SPRINT)
                }
            }
        }
    }
    when (trigger) {
        SINGLE_CLICK_LOCK -> if (newPointer) {
            status.sprintLocked = !status.sprintLocked
        }

        HOLD -> {
            if (clicked) {
                result.sprint = true
            }
        }
    }
}

fun Context.SprintButton(config: SprintButton) {
    RawSprintButton(
        trigger = config.trigger,
        texture = config.texture,
    )
}