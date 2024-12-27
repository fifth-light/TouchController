package top.fifthlight.touchcontroller.layout

import top.fifthlight.combine.paint.Color
import top.fifthlight.data.Offset
import top.fifthlight.data.Rect
import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.Joystick
import top.fifthlight.touchcontroller.state.PointerState
import kotlin.math.sqrt

fun Context.Joystick(layout: Joystick) {
    var currentPointer = pointers.values.firstOrNull {
        it.state is PointerState.Joystick
    }
    currentPointer?.let {
        for (pointer in pointers.values) {
            if (!pointer.inRect(size)) {
                continue
            }
            when (pointer.state) {
                PointerState.New -> pointer.state = PointerState.Invalid
                else -> {}
            }
        }
    } ?: run {
        for (pointer in pointers.values) {
            when (pointer.state) {
                PointerState.New -> {
                    if (!pointer.inRect(size)) {
                        continue
                    }
                    if (currentPointer != null) {
                        pointer.state = PointerState.Invalid
                    } else {
                        pointer.state = PointerState.Joystick
                        currentPointer = pointer
                    }
                }

                else -> {}
            }
        }
    }

    val rawOffset = currentPointer?.let { pointer ->
        pointer.scaledOffset / size.width.toFloat() * 2f - 1f
    }

    val normalizedOffset = rawOffset?.let { offset ->
        val squaredLength = offset.squaredLength
        if (squaredLength > 1) {
            val length = sqrt(squaredLength)
            offset / length
        } else {
            offset
        }
    }

    val opacityMultiplier = if (!layout.increaseOpacityWhenActive || currentPointer == null) {
        1f
    } else {
        1.5f
    }

    withOpacity(opacityMultiplier) {
        drawQueue.enqueue { canvas ->
            val color = Color(((0xFF * opacity).toInt() shl 24) or 0xFFFFFF)
            canvas.drawTexture(
                id = Textures.GUI_JOYSTICK_PAD,
                dstRect = Rect(size = size.toSize()),
                tint = color
            )
            val drawOffset = normalizedOffset ?: Offset.ZERO
            val stickSize = layout.stickSize()
            val actualOffset = ((drawOffset + 1f) / 2f * size) - stickSize.toSize() / 2f
            canvas.drawTexture(
                id = Textures.GUI_JOYSTICK_STICK,
                dstRect = Rect(
                    offset = actualOffset,
                    size = stickSize.toSize()
                ),
                tint = color
            )
        }
    }

    normalizedOffset?.let { (right, backward) ->
        if (layout.triggerSprint && rawOffset.y < -1.1f) {
            result.sprint = true
        }
        result.left = -right
        result.forward = -backward
    }
}