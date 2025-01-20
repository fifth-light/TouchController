package top.fifthlight.combine.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ChatAllowedCharacters
import org.koin.compose.KoinContext
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import top.fifthlight.combine.data.DataComponentTypeFactory
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
import top.fifthlight.combine.screen.LocalScreenFactory
import top.fifthlight.combine.screen.ScreenFactory
import top.fifthlight.combine.screen.ViewModel
import top.fifthlight.combine.sound.LocalSoundManager
import top.fifthlight.combine.util.CloseHandler
import top.fifthlight.combine.util.LocalCloseHandler
import top.fifthlight.data.IntSize
import top.fifthlight.data.Offset
import kotlin.coroutines.CoroutineContext
import kotlin.math.sign
import top.fifthlight.combine.data.Text as CombineText

val LocalScreen = staticCompositionLocalOf<GuiScreen> { error("No screen in context") }

private class ScreenCloseHandler(private val screen: CombineScreen) : CloseHandler {
    override fun close() {
        screen.close()
    }
}

private class CombineScreen(
    private val parent: GuiScreen?,
) : GuiScreen(), CoroutineScope {
    private val client = Minecraft.getMinecraft()
    private var initialized = false
    private val textMeasurer = TextMeasurerImpl(client.fontRenderer)
    private val dispatcher = GameDispatcherImpl(client)
    private val soundManager = SoundManagerImpl(client.soundHandler)
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
                    LocalDataComponentTypeFactory provides DataComponentTypeFactory.Unsupported,
                    LocalClipboard provides ClipboardHandlerImpl,
                    LocalScreenFactory provides ScreenFactoryImpl,
                ) {
                    content()
                }
            }
        }
    }

    override fun initGui() {
        super.initGui()
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

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        val mouseButton = mapMouseButton(button) ?: return
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
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, button: Int) {
        val mouseButton = mapMouseButton(button) ?: return
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
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        val scroll = Mouse.getEventDWheel()
        if (scroll != 0) {
            owner.onPointerEvent(
                PointerEvent(
                    id = 0,
                    position = Offset(
                        x = Mouse.getX().toFloat() / client.displayWidth * width,
                        y = Mouse.getY().toFloat() / client.displayHeight * height,
                    ),
                    pointerType = PointerType.Mouse,
                    button = null,
                    scrollDelta = Offset(
                        x = 0f,
                        y = scroll.sign.toFloat(),
                    ),
                    type = PointerEventType.Scroll
                )
            )
        }
    }

    var onDismissRequest: () -> Boolean = { false }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            if (!onDismissRequest()) {
                close()
            }
        }
        val key = mapKeyCode(keyCode)
        val modifier = mapModifier()
        owner.onKeyEvent(
            KeyEvent(
                key = key,
                modifier = modifier,
                pressed = true
            )
        )
        owner.onKeyEvent(
            KeyEvent(
                key = key,
                modifier = modifier,
                pressed = false
            )
        )
        if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
            owner.onTextInput(typedChar.toString())
        }
    }

    private var lastMouseX: Int = -1
    private var lastMouseY: Int = -1

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        if (mouseX != lastMouseX || mouseY != lastMouseY) {
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
            lastMouseX = mouseX
            lastMouseY = mouseY
        }

        val canvas = CanvasImpl(mc.fontRenderer)
        val context = RenderContext(canvas)

        val size = IntSize(width, height)
        owner.render(size, context)
    }

    fun close() {
        mc.displayGuiScreen(parent)
    }

    override fun onGuiClosed() {
        if (!ScreenFactoryImpl.screenSwitching) {
            owner.close()
        }
    }
}

object ScreenFactoryImpl : ScreenFactory {
    var screenSwitching = false

    override fun <M : ViewModel> openScreen(
        title: CombineText,
        viewModelFactory: (CoroutineScope, CloseHandler) -> M,
        onDismissRequest: (M) -> Boolean,
        content: @Composable (M) -> Unit
    ) {
        val client = Minecraft.getMinecraft()
        val screen = getScreen(client.currentScreen, title, viewModelFactory, onDismissRequest, content)
        screenSwitching = true
        client.displayGuiScreen(screen as GuiScreen)
        screenSwitching = false
    }

    override fun <M : ViewModel> getScreen(
        parent: Any?,
        title: CombineText,
        viewModelFactory: (CoroutineScope, CloseHandler) -> M,
        onDismissRequest: (M) -> Boolean,
        content: @Composable (M) -> Unit
    ): Any {
        val screen = CombineScreen(parent as GuiScreen)
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
