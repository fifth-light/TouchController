package top.fifthlight.touchcontroller.proxy.data

import kotlin.math.sqrt

data class Size(
    val width: Float,
    val height: Float,
) {
    companion object {
        val ZERO = Size(0f, 0f)
        val ONE = Size(1f, 1f)
    }

    constructor(size: Float): this(size, size)

    operator fun contains(offset: Offset): Boolean {
        val x = 0 <= offset.x && offset.x < width
        val y = 0 <= offset.y && offset.y < height
        return x && y
    }

    operator fun plus(length: Float) = Size(width = width + length, height = height + length)
    operator fun minus(offset: Offset) = Size(width = width - offset.x, height = height - offset.y)
    operator fun times(num: Float) = Size(width = width * num, height = height * num)
    operator fun div(num: Float) = Size(width = width / num, height = height / num)

    val squaredLength by lazy { width * width + height * height }
    val length by lazy { sqrt(squaredLength) }

    fun toIntSize() = IntSize(width = width.toInt(), height = height.toInt())
}
