package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.*
import top.fifthlight.touchcontroller.state.PointerState

private const val ID_FORWARD = "dpad_forward"
private const val ID_BACKWARD = "dpad_backward"
private const val ID_LEFT = "dpad_left"
private const val ID_RIGHT = "dpad_right"
private const val ID_LEFT_FORWARD = "dpad_left_forward"
private const val ID_RIGHT_FORWARD = "dpad_right_forward"
private const val ID_LEFT_BACKWARD = "dpad_left_backward"
private const val ID_RIGHT_BACKWARD = "dpad_right_backward"

fun Context.DPad(config: DPad) {
    val buttonSize = config.buttonSize()
    val buttonDisplaySize = config.buttonDisplaySize()
    val smallDisplaySize = if (config.classic) {
        config.smallButtonDisplaySize()
    } else {
        config.buttonDisplaySize()
    }
    val extraButtonDisplaySize = config.extraButtonDisplaySize()
    val offset = (buttonDisplaySize - smallDisplaySize) / 2

    val forward = withRect(
        x = buttonSize.width,
        y = 0,
        width = buttonSize.width,
        height = buttonSize.height
    ) {
        SwipeButton(id = ID_FORWARD) { clicked ->
            withAlign(
                align = Align.CENTER_CENTER,
                size = buttonDisplaySize
            ) {
                when (Pair(config.classic, clicked)) {
                    Pair(true, false) -> Texture(texture = Textures.GUI_DPAD_UP_CLASSIC)
                    Pair(true, true) -> Texture(texture = Textures.GUI_DPAD_UP_CLASSIC, color = 0xFFAAAAAAu)
                    Pair(false, false) -> Texture(texture = Textures.GUI_DPAD_UP)
                    Pair(false, true) -> Texture(texture = Textures.GUI_DPAD_UP_ACTIVE)
                }
            }
        }.clicked
    }

    val backward = withRect(
        x = buttonSize.width,
        y = buttonSize.height * 2,
        width = buttonSize.width,
        height = buttonSize.height
    ) {
        SwipeButton(id = ID_BACKWARD) { clicked ->
            withAlign(
                align = Align.CENTER_CENTER,
                size = buttonDisplaySize
            ) {
                when (Pair(config.classic, clicked)) {
                    Pair(true, false) -> Texture(texture = Textures.GUI_DPAD_DOWN_CLASSIC)
                    Pair(true, true) -> Texture(texture = Textures.GUI_DPAD_DOWN_CLASSIC, color = 0xFFAAAAAAu)
                    Pair(false, false) -> Texture(texture = Textures.GUI_DPAD_DOWN)
                    Pair(false, true) -> Texture(texture = Textures.GUI_DPAD_DOWN_ACTIVE)
                }
            }
        }.clicked
    }

    val left = withRect(
        x = 0,
        y = buttonSize.height,
        width = buttonSize.width,
        height = buttonSize.height
    ) {
        SwipeButton(id = ID_LEFT) { clicked ->
            withAlign(
                align = Align.CENTER_CENTER,
                size = buttonDisplaySize
            ) {
                when (Pair(config.classic, clicked)) {
                    Pair(true, false) -> Texture(texture = Textures.GUI_DPAD_LEFT_CLASSIC)
                    Pair(true, true) -> Texture(texture = Textures.GUI_DPAD_LEFT_CLASSIC, color = 0xFFAAAAAAu)
                    Pair(false, false) -> Texture(texture = Textures.GUI_DPAD_LEFT)
                    Pair(false, true) -> Texture(texture = Textures.GUI_DPAD_LEFT_ACTIVE)
                }
            }
        }.clicked
    }

    val right = withRect(
        x = buttonSize.width * 2,
        y = buttonSize.height,
        width = buttonSize.width,
        height = buttonSize.height
    ) {
        SwipeButton(id = ID_RIGHT) { clicked ->
            withAlign(
                align = Align.CENTER_CENTER,
                size = buttonDisplaySize
            ) {
                when (Pair(config.classic, clicked)) {
                    Pair(true, false) -> Texture(texture = Textures.GUI_DPAD_RIGHT_CLASSIC)
                    Pair(true, true) -> Texture(texture = Textures.GUI_DPAD_RIGHT_CLASSIC, color = 0xFFAAAAAAu)
                    Pair(false, false) -> Texture(texture = Textures.GUI_DPAD_RIGHT)
                    Pair(false, true) -> Texture(texture = Textures.GUI_DPAD_RIGHT_ACTIVE)
                }
            }
        }.clicked
    }

    val showLeftForward = forward || left || status.dpadLeftForwardShown
    val showRightForward = forward || right || status.dpadRightForwardShown
    val showLeftBackward = !config.classic && (backward || left || status.dpadLeftBackwardShown)
    val showRightBackward = !config.classic && (backward || right || status.dpadRightBackwardShown)

    val leftForward = if (showLeftForward) {
        withRect(
            x = 0,
            y = 0,
            width = buttonSize.width,
            height = buttonSize.height
        ) {
            SwipeButton(id = ID_LEFT_FORWARD) { clicked ->
                withAlign(
                    align = Align.RIGHT_BOTTOM,
                    size = smallDisplaySize,
                    offset = offset,
                ) {
                    when (Pair(config.classic, clicked)) {
                        Pair(true, false) -> Texture(texture = Textures.GUI_DPAD_UP_LEFT_CLASSIC)
                        Pair(true, true) -> Texture(texture = Textures.GUI_DPAD_UP_LEFT_CLASSIC, color = 0xFFAAAAAAu)
                        Pair(false, false) -> Texture(texture = Textures.GUI_DPAD_UP_LEFT)
                        Pair(false, true) -> Texture(texture = Textures.GUI_DPAD_UP_LEFT_ACTIVE)
                    }
                }
            }.clicked
        }
    } else {
        false
    }

    val rightForward = if (showRightForward) {
        withRect(
            x = buttonSize.width * 2,
            y = 0,
            width = buttonSize.width,
            height = buttonSize.height
        ) {
            SwipeButton(id = ID_RIGHT_FORWARD) { clicked ->
                withAlign(
                    align = Align.LEFT_BOTTOM,
                    size = smallDisplaySize,
                    offset = offset,
                ) {
                    when (Pair(config.classic, clicked)) {
                        Pair(true, false) -> Texture(texture = Textures.GUI_DPAD_UP_RIGHT_CLASSIC)
                        Pair(true, true) -> Texture(texture = Textures.GUI_DPAD_UP_RIGHT_CLASSIC, color = 0xFFAAAAAAu)
                        Pair(false, false) -> Texture(texture = Textures.GUI_DPAD_UP_RIGHT)
                        Pair(false, true) -> Texture(texture = Textures.GUI_DPAD_UP_RIGHT_ACTIVE)
                    }
                }
            }.clicked
        }
    } else {
        false
    }

    val leftBackward = if (showLeftBackward) {
        withRect(
            x = 0,
            y = buttonSize.height * 2,
            width = buttonSize.width,
            height = buttonSize.height
        ) {
            SwipeButton(id = ID_LEFT_BACKWARD) { clicked ->
                withAlign(
                    align = Align.RIGHT_TOP,
                    size = smallDisplaySize,
                    offset = offset,
                ) {
                    if (clicked) {
                        Texture(texture = Textures.GUI_DPAD_DOWN_LEFT_ACTIVE)
                    } else {
                        Texture(texture = Textures.GUI_DPAD_DOWN_LEFT)
                    }
                }
            }.clicked
        }
    } else {
        false
    }

    val rightBackward = if (showRightBackward) {
        withRect(
            x = buttonSize.width * 2,
            y = buttonSize.width * 2,
            width = buttonSize.width,
            height = buttonSize.height
        ) {
            SwipeButton(id = ID_RIGHT_BACKWARD) { clicked ->
                withAlign(
                    align = Align.LEFT_TOP,
                    size = smallDisplaySize,
                    offset = offset,
                ) {
                    if (clicked) {
                        Texture(texture = Textures.GUI_DPAD_DOWN_RIGHT_ACTIVE)
                    } else {
                        Texture(texture = Textures.GUI_DPAD_DOWN_RIGHT)
                    }
                }
            }.clicked
        }
    } else {
        false
    }

    status.dpadLeftForwardShown = left || forward || leftForward
    status.dpadRightForwardShown = right || forward || rightForward
    status.dpadLeftBackwardShown = !config.classic && (left || backward || leftBackward)
    status.dpadRightBackwardShown = !config.classic && (right || backward || rightBackward)

    when (Pair(forward || leftForward || rightForward, backward || leftBackward || rightBackward)) {
        Pair(true, false) -> result.forward = 1f
        Pair(false, true) -> result.forward = -1f
    }

    when (Pair(left || leftForward || leftBackward, right || rightForward || rightBackward)) {
        Pair(true, false) -> result.left = 1f
        Pair(false, true) -> result.left = -1f
    }

    withRect(
        x = buttonSize.width,
        y = buttonSize.height,
        width = buttonSize.width,
        height = buttonSize.height
    ) {
        val sneakButtonTexture = if (config.classic) {
            SneakButtonTexture.CLASSIC
        } else if (config.padding < 0 && config.extraButtonDisplaySize() == buttonSize) {
            SneakButtonTexture.NEW_DPAD
        } else {
            SneakButtonTexture.NEW
        }
        when (config.extraButton) {
            DPadExtraButton.NONE -> {}
            DPadExtraButton.SNEAK_DOUBLE_CLICK -> RawSneakButton(
                texture = sneakButtonTexture,
                trigger = SneakButtonTrigger.DOUBLE_CLICK_LOCK,
                size = extraButtonDisplaySize
            )

            DPadExtraButton.SNEAK_SINGLE_CLICK -> RawSneakButton(
                texture = sneakButtonTexture,
                trigger = SneakButtonTrigger.SINGLE_CLICK_LOCK,
                size = extraButtonDisplaySize
            )

            DPadExtraButton.SNEAK_HOLD -> RawSneakButton(
                texture = sneakButtonTexture,
                trigger = SneakButtonTrigger.HOLD,
                size = extraButtonDisplaySize
            )

            DPadExtraButton.DISMOUNT_SINGLE_CLICK -> RawSneakButton(
                texture = if (config.classic) {
                    SneakButtonTexture.CLASSIC
                } else {
                    SneakButtonTexture.DISMOUNT_DPAD
                },
                trigger = SneakButtonTrigger.SINGLE_CLICK_TRIGGER,
                size = extraButtonDisplaySize
            )

            DPadExtraButton.DISMOUNT_DOUBLE_CLICK -> RawSneakButton(
                texture = if (config.classic) {
                    SneakButtonTexture.CLASSIC
                } else {
                    SneakButtonTexture.DISMOUNT_DPAD
                },
                trigger = SneakButtonTrigger.DOUBLE_CLICK_TRIGGER,
                size = extraButtonDisplaySize
            )

            DPadExtraButton.JUMP, DPadExtraButton.FLYING -> {
                val hasForward = pointers.values.any {
                    (it.state as? PointerState.SwipeButton)?.id == ID_FORWARD
                }
                val jumpClicked = DPadJumpButton(
                    size = extraButtonDisplaySize,
                    texture = if (!config.classic) {
                        JumpButtonTexture.NEW
                    } else {
                        when (config.extraButton) {
                            DPadExtraButton.JUMP -> JumpButtonTexture.CLASSIC
                            DPadExtraButton.FLYING -> JumpButtonTexture.CLASSIC_FLYING
                            else -> error("Unreachable")
                        }
                    }
                )
                if (jumpClicked) {
                    if (hasForward) {
                        result.forward = 1f
                        if (!status.dpadForwardJumping) {
                            status.jumping = true
                            status.dpadForwardJumping = true
                        }
                    } else {
                        status.jumping = true
                    }
                } else {
                    status.dpadForwardJumping = false
                }
            }
        }
    }
}