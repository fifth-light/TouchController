package top.fifthlight.combine.input

import androidx.compose.runtime.Immutable
import top.fifthlight.data.IntSize
import top.fifthlight.data.Offset

@JvmInline
value class PointerButton private constructor(internal val value: Int) {
    companion object {
        val Unknown = PointerButton(0)
        val Left = PointerButton(1)
        val Middle = PointerButton(2)
        val Right = PointerButton(3)
    }

    override fun toString(): String =
        when (this) {
            Left -> "Left"
            Middle -> "Middle"
            Right -> "Right"
            else -> "Unknown"
        }
}

@JvmInline
value class PointerEventType private constructor(internal val value: Int) {
    companion object {
        val Unknown = PointerEventType(0)
        val Press = PointerEventType(1)
        val Release = PointerEventType(2)
        val Move = PointerEventType(3)
        val Scroll = PointerEventType(4)
    }

    override fun toString(): String =
        when (this) {
            Press -> "Press"
            Release -> "Release"
            Move -> "Move"
            Scroll -> "Scroll"
            else -> "Unknown"
        }
}

@JvmInline
value class PointerType private constructor(internal val value: Int) {
    companion object {
        val Unknown = PointerType(0)
        val Mouse = PointerType(1)
        val Touch = PointerType(2)
    }

    override fun toString(): String =
        when (this) {
            Mouse -> "Mouse"
            Touch -> "Touch"
            else -> "Unknown"
        }
}

@Immutable
data class PointerInputChange(
    val id: Int,
    val position: Offset,
    val type: PointerType = PointerType.Mouse,
    val button: PointerButton? = null,
    val scrollDelta: Offset = Offset.ZERO
)

data class PointerEvent(
    val changes: List<PointerInputChange>,
    var type: PointerEventType
)

interface AwaitPointerEventScope {
    val size: IntSize

    val currentEvent: PointerEvent

    suspend fun awaitPointerEvent(): PointerEvent

    suspend fun <T> withTimeoutOrNull(
        timeMillis: Long,
        block: suspend AwaitPointerEventScope.() -> T
    ): T? = block()

    suspend fun <T> withTimeout(
        timeMillis: Long,
        block: suspend AwaitPointerEventScope.() -> T
    ): T = block()
}

interface PointerInputScope {
    var interceptOutOfBoundsChildEvents: Boolean
        get() = false
        set(_) {}

    suspend fun <R> awaitPointerEventScope(block: suspend AwaitPointerEventScope.() -> R): R
}