package top.fifthlight.data

import kotlinx.serialization.Serializable
import kotlin.math.sqrt

fun Size(size: Float) = Size(packFloats(size, size))
fun Size(width: Float, height: Float) = Size(packFloats(width, height))

@Serializable
@JvmInline
value class Size internal constructor(private val packed: Long) {
    val width
        get() = unpackFloat1(packed)

    val height
        get() = unpackFloat2(packed)

    companion object {
        val ZERO = Size(0f, 0f)
        val ONE = Size(1f, 1f)
    }

    operator fun contains(offset: Offset): Boolean {
        val x = 0 <= offset.x && offset.x < width
        val y = 0 <= offset.y && offset.y < height
        return x && y
    }

    fun toIntSize() = IntSize(width = width.toInt(), height = height.toInt())

    operator fun component1() = width
    operator fun component2() = height
    operator fun plus(length: Float) = Size(width = width + length, height = height + length)
    operator fun minus(offset: Offset) = Size(width = width - offset.x, height = height - offset.y)
    operator fun times(num: Float) = Size(width = width * num, height = height * num)
    operator fun div(num: Float) = Size(width = width / num, height = height / num)

    val squaredLength
        get() = width * width + height * height
    val length
        get() = sqrt(squaredLength)

    override fun toString(): String {
        return "Size(width=$width, height=$height)"
    }
}
