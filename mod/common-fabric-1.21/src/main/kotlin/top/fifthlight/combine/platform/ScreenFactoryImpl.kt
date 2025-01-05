package top.fifthlight.combine.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.koin.compose.KoinContext
import org.lwjgl.glfw.GLFW
import top.fifthlight.combine.data.LocalDataComponentTypeFactory
import top.fifthlight.combine.data.LocalItemFactory
import top.fifthlight.combine.data.LocalTextFactory
import top.fifthlight.combine.input.input.LocalClipboard
import top.fifthlight.combine.input.key.KeyEvent
import top.fifthlight.combine.input.pointer.PointerButton
import top.fifthlight.combine.input.pointer.PointerEvent
import top.fifthlight.combine.input.pointer.PointerEventType
import top.fifthlight.combine.input.pointer.PointerType
import top.fifthlight.combine.node.CombineOwner
import top.fifthlight.combine.paint.RenderContext
import top.fifthlight.combine.platform.mapKeyCode
import top.fifthlight.combine.platform.mapModifier
import top.fifthlight.combine.screen.LocalScreenFactory
import top.fifthlight.combine.screen.ScreenFactory
import top.fifthlight.combine.screen.ViewModel
import top.fifthlight.combine.sound.LocalSoundManager
import top.fifthlight.combine.util.CloseHandler
import top.fifthlight.combine.util.LocalCloseHandler
import top.fifthlight.data.IntSize
import top.fifthlight.data.Offset
import kotlin.coroutines.CoroutineContext
import top.fifthlight.combine.data.Text as CombineText

val LocalScreen = staticCompositionLocalOf<Screen> { error("No screen in context") }

private class ScreenCloseHandler(private val screen: Screen) : CloseHandler {
    override fun close() {
        screen.close()
    }
}

private class CombineScreen(
    title: Text,
    private val parent: Screen?,
) : Screen(title), CoroutineScope {
    private val currentClient = MinecraftClient.getInstance()
    private var initialized = false
    private val textMeasurer = TextMeasurerImpl(currentClient.textRenderer)
    private val dispatcher = GameDispatcherImpl(currentClient)
    private val soundManager = SoundManagerImpl(currentClient.soundManager)
    val closeHandler = ScreenCloseHandler(this@CombineScreen)
    private val owner = CombineOwner(dispatcher = dispatcher, textMeasurer = textMeasurer)
    override val coroutineContext: CoroutineContext
        get() = owner.coroutineContext

    fun setContent(content: @Composable () -> Unit) {
        owner.setContent {
            KoinContext {
                CompositionLocalProvider(
                    LocalSoundManager provides soundManager,
                    LocalScreen provides this,
                    LocalCloseHandler provides closeHandler,
                    LocalItemFactory provides ItemFactoryImpl,
                    LocalTextFactory provides TextFactoryImpl,
                    LocalDataComponentTypeFactory provides DataComponentTypeFactoryImpl,
                    LocalClipboard provides ClipboardHandlerImpl,
                    LocalScreenFactory provides ScreenFactoryImpl,
                ) {
                    content()
                }
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

    private fun mapMouseButton(button: Int) = when (button) {
        0 -> PointerButton.Left
        1 -> PointerButton.Middle
        2 -> PointerButton.Right
        else -> null
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val mouseButton = mapMouseButton(button) ?: return true
        owner.onPointerEvent(
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
        owner.onPointerEvent(
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
        owner.onPointerEvent(
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
        owner.onPointerEvent(
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

    override fun charTyped(char: Char, modifiers: Int): Boolean {
        owner.onTextInput(char.toString())
        return true
    }

    var onDismissRequest: () -> Boolean = { false }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (!onDismissRequest()) {
                close()
            }
            return true
        }
        owner.onKeyEvent(
            KeyEvent(
                key = mapKeyCode(keyCode),
                modifier = mapModifier(modifiers),
                pressed = true
            )
        )
        return true
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        owner.onKeyEvent(
            KeyEvent(
                key = mapKeyCode(keyCode),
                modifier = mapModifier(modifiers),
                pressed = false
            )
        )
        return true
    }

    override fun render(drawContext: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(drawContext, mouseX, mouseY, delta)

        val client = client!!
        val canvas = CanvasImpl(drawContext, client.textRenderer)
        val context = RenderContext(canvas)

        val size = IntSize(width, height)
        owner.render(size, context)
    }

    override fun close() {
        owner.close()
        client?.setScreen(parent)
    }
}

object ScreenFactoryImpl : ScreenFactory {
    override fun <M : ViewModel> openScreen(
        title: CombineText,
        viewModelFactory: (CoroutineScope, CloseHandler) -> M,
        onDismissRequest: (M) -> Boolean,
        content: @Composable (M) -> Unit
    ) {
        val client = MinecraftClient.getInstance()
        val screen = getScreen(client.currentScreen, title, viewModelFactory, onDismissRequest, content)
        client.setScreen(screen as Screen)
    }

    override fun <M : ViewModel> getScreen(
        parent: Any?,
        title: CombineText,
        viewModelFactory: (CoroutineScope, CloseHandler) -> M,
        onDismissRequest: (M) -> Boolean,
        content: @Composable (M) -> Unit
    ): Any {
        val screen = CombineScreen(title.toMinecraft(), parent as Screen)
        val viewModel = viewModelFactory(screen, screen.closeHandler)
        screen.onDismissRequest = {
            onDismissRequest(viewModel)
        }
        screen.setContent {
            content(viewModel)
        }
        return screen
    }
}
