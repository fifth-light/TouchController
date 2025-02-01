package top.fifthlight.touchcontroller.layout

import kotlinx.collections.immutable.PersistentMap
import org.koin.core.component.KoinComponent
import top.fifthlight.combine.paint.Canvas
import top.fifthlight.combine.paint.withTranslate
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntRect
import top.fifthlight.data.IntSize
import top.fifthlight.data.Offset
import top.fifthlight.touchcontroller.config.GlobalConfig
import top.fifthlight.touchcontroller.config.LayerConditionKey
import top.fifthlight.touchcontroller.gal.KeyBindingHandler
import top.fifthlight.touchcontroller.state.Pointer

data class DoubleClickState(private val clickTime: Int = 15) {
    private var lastClick: Int = -1

    fun click(time: Int): Boolean {
        if (lastClick == -1) {
            lastClick = time
            return false
        }

        val interval = time - lastClick
        lastClick = time
        val doubleClicked = interval <= clickTime
        if (doubleClicked) {
            lastClick = -1
        }

        return doubleClicked
    }

    fun clear() {
        this.lastClick = -1
    }
}

data class InventorySlotStatus(
    var progress: Float = 0f,
    var drop: Boolean = false,
    var select: Boolean = false,
)

data class InventoryResult(
    val slots: Array<InventorySlotStatus> = Array(9) { InventorySlotStatus() }
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InventoryResult

        return slots.contentEquals(other.slots)
    }

    override fun hashCode(): Int {
        return slots.contentHashCode()
    }
}

data class ContextResult(
    var forward: Float = 0f,
    var left: Float = 0f,
    var pause: Boolean = false,
    var chat: Boolean = false,
    var lookDirection: Offset? = null,
    var crosshairStatus: CrosshairStatus? = null,
    var cancelFlying: Boolean = false,
    val inventory: InventoryResult = InventoryResult(),
    var boatLeft: Boolean = false,
    var boatRight: Boolean = false,
    var showBlockOutline: Boolean = false,
)

enum class DPadDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT,
}

data class ContextStatus(
    var dpadLeftForwardShown: Boolean = false,
    var dpadRightForwardShown: Boolean = false,
    var dpadLeftBackwardShown: Boolean = false,
    var dpadRightBackwardShown: Boolean = false,
    var dpadJumping: Boolean = false,
    val cancelFlying: DoubleClickState = DoubleClickState(),
    val sneakLocking: DoubleClickState = DoubleClickState(),
    val sneakTrigger: DoubleClickState = DoubleClickState(),
    var lastCrosshairStatus: CrosshairStatus? = null,
    var vibrate: Boolean = false,
    val quickHandSwap: DoubleClickState = DoubleClickState(7),
    var lastDpadDirection: DPadDirection? = null,
    var wasSprinting: Boolean = false,
)

data class ContextCounter(
    var tick: Int = 0,
) {
    fun tick() {
        tick++
    }
}

data class Context(
    val windowSize: IntSize,
    val windowScaledSize: IntSize,
    val inGui: Boolean = false,
    val drawQueue: DrawQueue = DrawQueue(),
    val size: IntSize,
    val screenOffset: IntOffset,
    val opacity: Float = 1f,
    val pointers: MutableMap<Int, Pointer> = mutableMapOf(),
    val condition: PersistentMap<LayerConditionKey, Boolean>,
    val result: ContextResult = ContextResult(),
    val status: ContextStatus = ContextStatus(),
    val keyBindingHandler: KeyBindingHandler = KeyBindingHandler.Empty,
    val timer: ContextCounter = ContextCounter(),
    val config: GlobalConfig,
) : KoinComponent {
    inline fun <reified T> transformDrawQueue(
        crossinline drawTransform: Canvas.(block: () -> Unit) -> Unit = { it() },
        crossinline contextTransform: Context.(DrawQueue) -> Context = { copy(drawQueue = it) },
        crossinline block: Context.() -> T
    ): T {
        val newQueue = DrawQueue()
        val newContext = contextTransform(this, newQueue)
        val result = newContext.block()
        drawQueue.enqueue { canvas ->
            canvas.drawTransform {
                newQueue.execute(canvas)
            }
        }
        return result
    }

    inline fun <reified T> withOffset(offset: IntOffset, crossinline block: Context.() -> T): T =
        transformDrawQueue(
            drawTransform = { withTranslate(offset.x, offset.y) { it() } },
            contextTransform = { newQueue ->
                copy(
                    screenOffset = screenOffset + offset,
                    size = size - offset,
                    drawQueue = newQueue
                )
            },
            block
        )

    inline fun <reified T> withOffset(x: Int, y: Int, crossinline block: Context.() -> T): T =
        withOffset(IntOffset(x, y), block)

    inline fun <reified T> withSize(size: IntSize, crossinline block: Context.() -> T): T = copy(size = size).block()

    inline fun <reified T> withRect(x: Int, y: Int, width: Int, height: Int, crossinline block: Context.() -> T): T =
        transformDrawQueue(
            drawTransform = { withTranslate(x, y) { it() } },
            contextTransform = { newQueue ->
                copy(
                    screenOffset = screenOffset + IntOffset(x, y),
                    size = IntSize(width, height),
                    drawQueue = newQueue
                )
            },
            block
        )

    inline fun <reified T> withRect(offset: IntOffset, size: IntSize, crossinline block: Context.() -> T): T =
        transformDrawQueue(
            drawTransform = { withTranslate(offset.x, offset.y) { it() } },
            contextTransform = { newQueue ->
                copy(
                    screenOffset = screenOffset + offset,
                    size = size,
                    drawQueue = newQueue
                )
            },
            block
        )

    inline fun <reified T> withRect(rect: IntRect, crossinline block: Context.() -> T): T =
        withRect(rect.offset, rect.size, block)

    inline fun <reified T> withOpacity(opacity: Float, crossinline block: Context.() -> T): T =
        transformDrawQueue(
            contextTransform = { newQueue ->
                copy(
                    drawQueue = newQueue,
                    opacity = (this.opacity * opacity).coerceAtMost(1f)
                )
            },
            block = block
        )

    val Pointer.rawOffset: Offset
        get() = position * windowSize

    val Pointer.scaledOffset: Offset
        get() = position * windowScaledSize - screenOffset

    fun Pointer.inRect(size: IntSize): Boolean = scaledOffset in size

    fun getPointersInRect(size: IntSize): List<Pointer> = pointers.values.filter { it.inRect(size) }
}