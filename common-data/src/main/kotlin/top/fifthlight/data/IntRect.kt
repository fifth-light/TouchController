package top.fifthlight.data

import kotlinx.serialization.Serializable

@Serializable
data class IntRect(
    val offset: IntOffset,
    val size: IntSize
) {
    val left
        get() = offset.x
    val top
        get() = offset.y
    val right
        get() = offset.x + size.width
    val bottom
        get() = offset.y + size.height

    companion object {
        val ZERO = IntRect(offset = IntOffset.ZERO, size = IntSize.ZERO)
    }

    fun toRect() = Rect(
        offset = offset.toOffset(),
        size = size.toSize()
    )
}