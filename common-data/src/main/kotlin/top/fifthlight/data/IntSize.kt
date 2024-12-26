package top.fifthlight.data

import kotlinx.serialization.Serializable

@Serializable
data class IntSize(
    val width: Int,
    val height: Int,
) {
    companion object {
        val ZERO = IntSize(0, 0)
    }

    constructor(size: Int): this(size, size)

    operator fun contains(offset: Offset): Boolean {
        val x = 0 <= offset.x && offset.x < width
        val y = 0 <= offset.y && offset.y < height
        return x && y
    }

    operator fun contains(offset: IntOffset): Boolean {
        val x = offset.x in 0..<width
        val y = offset.y in 0..<height
        return x && y
    }

    operator fun plus(length: Int) = IntSize(width = width + length, height = height + length)
    operator fun minus(size: Int) = IntSize(width = width - size, height = height - size)
    operator fun minus(size: IntSize) = IntOffset(x = width - size.width, y = height - size.height)
    operator fun minus(offset: IntOffset) = IntSize(width = width - offset.x, height = height - offset.y)
    operator fun times(num: Int) = IntSize(width = width * num, height = height * num)
    operator fun div(num: Int) = IntSize(width = width / num, height = height / num)
    fun toSize() = Size(width = width.toFloat(), height = height.toFloat())
}