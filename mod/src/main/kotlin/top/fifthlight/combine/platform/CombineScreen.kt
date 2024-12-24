package top.fifthlight.combine.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import top.fifthlight.combine.input.PointerButton
import top.fifthlight.combine.input.PointerEvent
import top.fifthlight.combine.input.PointerEventType
import top.fifthlight.combine.input.PointerType
import top.fifthlight.combine.node.CombineOwner
import top.fifthlight.combine.paint.RenderContext
import top.fifthlight.combine.sound.LocalSoundManager
import top.fifthlight.combine.util.CloseHandler
import top.fifthlight.combine.util.LocalCloseHandler
import top.fifthlight.data.IntSize
import top.fifthlight.data.Offset
import kotlin.coroutines.CoroutineContext

val LocalScreen: ProvidableCompositionLocal<Screen> = staticCompositionLocalOf { error("No screen in context") }

private class ScreenCloseHandler(private val screen: Screen): CloseHandler {
    override fun close() {
        screen.close()
    }
}

open class CombineScreen(
    title: Text,
    private val parent: Screen?
) : Screen(title), CoroutineScope {
    private val currentClient = MinecraftClient.getInstance()
    private var initialized = false
    private val textMeasurer = MinecraftTextMeasurer(currentClient.textRenderer)
    private val dispatcher = MinecraftDispatcher(currentClient)
    private val soundManager = MinecraftSoundManager(currentClient.soundManager)
    private val owner = CombineOwner(dispatcher = dispatcher, textMeasurer = textMeasurer)
    override val coroutineContext: CoroutineContext
        get() = owner.coroutineContext

    fun setContent(content: @Composable () -> Unit) {
        owner.setContent {
            CompositionLocalProvider(
                LocalSoundManager provides soundManager,
                LocalScreen provides this,
                LocalCloseHandler provides ScreenCloseHandler(this@CombineScreen)
            ) {
                content()
            }
        }
    }

    override fun init() {
        super.init()
        if (!initialized) {
            initialized = true
            owner.start()
        }
    }

    private val pendingInputEvents = ArrayDeque<PointerEvent>()

    private fun mapMouseButton(button: Int) = when (button) {
        0 -> PointerButton.Left
        1 -> PointerButton.Middle
        2 -> PointerButton.Right
        else -> null
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val mouseButton = mapMouseButton(button) ?: return true
        pendingInputEvents.add(
            PointerEvent(
                id = 0,
                position = Offset(
                    x = mouseX.toFloat(),
                    y = mouseY.toFloat(),
                ),
                pointerType = PointerType.Mouse,
                button = mouseButton,
                type = PointerEventType.Press
            )
        )
        return true
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val mouseButton = mapMouseButton(button) ?: return true
        pendingInputEvents.add(
            PointerEvent(
                id = 0,
                position = Offset(
                    x = mouseX.toFloat(),
                    y = mouseY.toFloat(),
                ),
                pointerType = PointerType.Mouse,
                button = mouseButton,
                type = PointerEventType.Release
            )
        )
        return true
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        pendingInputEvents.add(
            PointerEvent(
                id = 0,
                position = Offset(
                    x = mouseX.toFloat(),
                    y = mouseY.toFloat(),
                ),
                pointerType = PointerType.Mouse,
                button = null,
                type = PointerEventType.Move
            )
        )
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        pendingInputEvents.add(
            PointerEvent(
                id = 0,
                position = Offset(
                    x = mouseX.toFloat(),
                    y = mouseY.toFloat(),
                ),
                pointerType = PointerType.Mouse,
                button = null,
                scrollDelta = Offset(
                    x = horizontalAmount.toFloat(),
                    y = verticalAmount.toFloat(),
                ),
                type = PointerEventType.Scroll
            )
        )
        return true
    }

    override fun render(drawContext: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(drawContext, mouseX, mouseY, delta)

        while (true) {
            val event = pendingInputEvents.removeFirstOrNull() ?: break
            owner.onPointerEvent(event)
        }

        val client = client!!
        val canvas = MinecraftCanvas(drawContext, client.textRenderer)
        val context = RenderContext(canvas)

        val size = IntSize(width, height)
        owner.render(size, context)
    }

    override fun close() {
        super.close()
        owner.close()
        parent?.let { client?.setScreen(it) }
    }
}
