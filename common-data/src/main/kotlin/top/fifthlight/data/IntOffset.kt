package top.fifthlight.data

import kotlinx.serialization.Serializable

@Serializable
data class IntOffset(
    val x: Int,
    val y: Int,
) {
    val left
        get() = x
    val top
        get() = y

    companion object {
        val ZERO = IntOffset(0, 0)
    }

    fun inRegion(offset: IntOffset, size: IntSize): Boolean {
        val x = offset.x <= x && x < offset.x + size.width
        val y = offset.y <= y && y < offset.y + size.height
        return x && y
    }

    fun toOffset() = Offset(x = x.toFloat(), y = y.toFloat())

    operator fun plus(length: Int) = IntOffset(x = x + length, y = y + length)
    operator fun plus(other: IntSize) = IntOffset(
        x = x + other.width,
        y = y + other.height
    )
    operator fun plus(other: IntOffset) = IntOffset(
        x = x + other.x,
        y = y + other.y
    )
    operator fun minus(length: Int) = IntOffset(x = x - length, y = y - length)
    operator fun minus(other: IntSize) = IntOffset(
        x = x - other.width,
        y = y - other.height
    )
    operator fun minus(other: IntOffset) = IntOffset(
        x = x - other.x,
        y = y - other.y
    )
    operator fun times(num: Int) = IntOffset(x = x * num, y = y * num)
    operator fun div(num: Int) = IntOffset(x = x / num, y = y / num)

    override fun toString(): String {
        return "IntOffset(left=$x, top=$y)"
    }
}
