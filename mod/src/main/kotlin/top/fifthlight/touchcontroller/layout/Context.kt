package top.fifthlight.touchcontroller.layout

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.Window
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.config.TouchControllerConfig
import top.fifthlight.touchcontroller.ext.scaledSize
import top.fifthlight.touchcontroller.ext.size
import top.fifthlight.touchcontroller.ext.withTranslate
import top.fifthlight.touchcontroller.proxy.data.IntOffset
import top.fifthlight.touchcontroller.proxy.data.IntRect
import top.fifthlight.touchcontroller.proxy.data.IntSize
import top.fifthlight.touchcontroller.proxy.data.Offset
import top.fifthlight.touchcontroller.state.Pointer

data class KeyBindingResult(
    var timesPressed: Int = 0,
    var isPressed: Boolean = false
) {
    // Click for once, don't hold this key
    fun click() {
        timesPressed++
    }

    // Hold this key
    fun press() {
        if (!isPressed) {
            timesPressed++
        }
        isPressed = true
    }

    // Release this key
    fun release() {
        isPressed = false
    }

    fun wasPressed(): Boolean {
        if (timesPressed > 0) {
            timesPressed--
            return true
        } else {
            return false
        }
    }
}

data class DoubleClickState(
    private var lastClick: Int = -1
) {
    fun click(time: Int): Boolean {
        if (lastClick == -1) {
            lastClick = time
            return false
        }

        val interval = time - lastClick
        lastClick = time
        val doubleClicked = interval <= 15
        if (doubleClicked) {
            lastClick = -1
        }

        return doubleClicked
    }

    fun clear() {
        this.lastClick = -1
    }
}

enum class HudState {
    SWIMMING,
    FLYING,
    NORMAL
}

data class ContextResult(
    var forward: Float = 0f,
    var left: Float = 0f,
    var pause: Boolean = false,
    var chat: Boolean = false,
    var lookDirection: Offset? = null,
    var crosshairStatus: CrosshairStatus? = null,
    var sneak: Boolean = false,
    var cancelFlying: Boolean = false,
)

data class ContextStatus(
    var dpadLeftForwardShown: Boolean = false,
    var dpadRightForwardShown: Boolean = false,
    var dpadLeftBackwardShown: Boolean = false,
    var dpadRightBackwardShown: Boolean = false,
    var dpadForwardJumping: Boolean = false,
    var sneakLocked: Boolean = false,
    val cancelFlying: DoubleClickState = DoubleClickState(),
    val sneakLocking: DoubleClickState = DoubleClickState(),
    var jumping: Boolean = false,
    val attack: KeyBindingResult = KeyBindingResult(),
    val itemUse: KeyBindingResult = KeyBindingResult(),
    var lastCrosshairStatus: CrosshairStatus? = null,
    val openInventory: KeyBindingResult = KeyBindingResult()
)

data class ContextCounter(
    var tick: Int = 0,
) {
    fun tick() {
        tick++
    }
}

data class Context(
    val drawQueue: DrawQueue = DrawQueue(),
    val size: IntSize,
    val screenOffset: IntOffset,
    val scale: Float,
    val opacity: Float = 1f,
    val pointers: MutableMap<Int, Pointer> = mutableMapOf(),
    val result: ContextResult = ContextResult(),
    val status: ContextStatus = ContextStatus(),
    val timer: ContextCounter = ContextCounter(),
    val designMode: Boolean = false,
    val state: HudState = HudState.NORMAL,
    val config: TouchControllerConfig
) : KoinComponent {
    val client: MinecraftClient by inject()
    val window: Window
        get() = client.window

    inline fun <reified T> transformDrawQueue(
        crossinline drawTransform: DrawContext.(block: () -> Unit) -> Unit = { it() },
        crossinline contextTransform: Context.(DrawQueue) -> Context = { copy(drawQueue = it) },
        crossinline block: Context.() -> T
    ): T {
        val newQueue = DrawQueue()
        val newContext = contextTransform(this, newQueue)
        val result = newContext.block()
        drawQueue.enqueue { drawContext, textRenderer ->
            drawContext.drawTransform {
                newQueue.execute(drawContext, textRenderer)
            }
        }
        return result
    }

    inline fun <reified T> withOffset(offset: IntOffset, crossinline block: Context.() -> T): T =
        transformDrawQueue(
            drawTransform = { withTranslate(offset.x.toFloat(), offset.y.toFloat(), it) },
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
            drawTransform = { withTranslate(x.toFloat(), y.toFloat(), it) },
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
            drawTransform = { withTranslate(offset.x.toFloat(), offset.y.toFloat(), it) },
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
                    opacity = this.opacity * opacity
                )
            },
            block = block
        )

    val Pointer.rawOffset: Offset
        get() = position * window.size

    val Pointer.scaledOffset: Offset
        get() = position * window.scaledSize - screenOffset

    fun Pointer.inRect(size: IntSize): Boolean = scaledOffset in size

    fun getPointersInRect(size: IntSize): List<Pointer> = pointers.values.filter { it.inRect(size) }
}