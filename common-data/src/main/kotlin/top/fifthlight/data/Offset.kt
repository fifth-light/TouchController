package top.fifthlight.data

import kotlinx.serialization.Serializable
import kotlin.math.sqrt

@Serializable
data class Offset(
    val x: Float,
    val y: Float
) {
    constructor(offset: Float) : this(offset, offset)

    val left
        get() = x
    val top
        get() = y

    companion object {
        val ZERO = Offset(0f, 0f)
    }

    fun toIntOffset() = IntOffset(x = x.toInt(), y = y.toInt())

    operator fun plus(length: Float) = Offset(x = x + length, y = y + length)
    operator fun plus(offset: Offset) = Offset(x = x + offset.x, y = y + offset.y)
    operator fun minus(length: Float) = Offset(x = x - length, y = y - length)
    operator fun div(num: Float) = Offset(x = x / num, y = y / num)
    operator fun times(num: Float): Offset = Offset(x = x * num, y = y * num)
    operator fun minus(offset: IntOffset) = Offset(x = x - offset.x, y = y - offset.y)
    operator fun minus(offset: Offset) = Offset(x = x - offset.x, y = y - offset.y)
    operator fun minus(size: Size) = Offset(x = x - size.width, y = y - size.height)
    operator fun times(size: IntSize) = Offset(x = x * size.width.toFloat(), y = y * size.height.toFloat())

    val squaredLength by lazy { x * x + y * y }
    val length by lazy { sqrt(squaredLength) }
}
