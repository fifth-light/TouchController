package top.fifthlight.touchcontroller.layout

import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize

enum class Align {
    LEFT_TOP,
    CENTER_TOP,
    RIGHT_TOP,
    LEFT_CENTER,
    CENTER_CENTER,
    RIGHT_CENTER,
    LEFT_BOTTOM,
    CENTER_BOTTOM,
    RIGHT_BOTTOM;

    fun alignOffset(windowSize: IntSize, size: IntSize, offset: IntOffset): IntOffset {
        return when (this) {
            LEFT_TOP -> offset

            CENTER_TOP -> IntOffset(
                x = (windowSize.width - size.width) / 2 + offset.x,
                y = offset.y
            )

            RIGHT_TOP -> IntOffset(
                x = windowSize.width - size.width - offset.x,
                y = offset.y,
            )

            LEFT_CENTER -> IntOffset(
                x = offset.x,
                y = (windowSize.height - size.height) / 2 + offset.y
            )

            CENTER_CENTER -> (windowSize - size) / 2 + offset

            RIGHT_CENTER -> IntOffset(
                x = windowSize.width - size.width - offset.x,
                y = (windowSize.height - size.height) / 2 + offset.y
            )

            LEFT_BOTTOM -> IntOffset(
                x = offset.x,
                y = windowSize.height - size.height - offset.y,
            )

            CENTER_BOTTOM -> IntOffset(
                x = (windowSize.width - size.width) / 2 + offset.x,
                y = windowSize.height - size.height - offset.y,
            )

            RIGHT_BOTTOM -> IntOffset(
                x = windowSize.width - size.width - offset.x,
                y = windowSize.height - size.height - offset.y,
            )
        }
    }
}

inline fun <reified T> Context.withAlign(
    align: Align,
    size: IntSize,
    offset: IntOffset = IntOffset.ZERO,
    crossinline block: Context.() -> T
): T =
    withRect(
        offset = align.alignOffset(windowSize = this.size, offset = offset, size = size),
        size = size,
        block = block
    )