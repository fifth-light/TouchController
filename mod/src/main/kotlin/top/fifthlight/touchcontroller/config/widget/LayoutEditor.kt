package top.fifthlight.touchcontroller.config.widget

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Colors
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize
import top.fifthlight.data.Offset
import top.fifthlight.touchcontroller.config.ObservableValue
import top.fifthlight.touchcontroller.config.TouchControllerConfigHolder
import top.fifthlight.touchcontroller.config.TouchControllerLayout
import top.fifthlight.touchcontroller.config.replaceItem
import top.fifthlight.touchcontroller.control.ControllerWidget
import top.fifthlight.touchcontroller.ext.withBlend
import top.fifthlight.touchcontroller.ext.withBlendFunction
import top.fifthlight.touchcontroller.ext.withTranslate
import top.fifthlight.touchcontroller.layout.Align.*
import top.fifthlight.touchcontroller.layout.Context
import top.fifthlight.touchcontroller.layout.ContextResult
import top.fifthlight.touchcontroller.layout.DrawQueue
import top.fifthlight.touchcontroller.layout.withAlign

class LayoutEditor(
    x: Int = 0,
    y: Int = 0,
    width: Int = 0,
    height: Int = 0,
    message: Text? = null,
    private val layoutConfig: ObservableValue<TouchControllerLayout>,
    private val selectedConfig: ObservableValue<ControllerWidget?>,
) : ClickableWidget(x, y, width, height, message), KoinComponent {
    private val client: MinecraftClient by inject()
    private val config: TouchControllerConfigHolder by inject()

    override fun renderWidget(drawContext: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val config = config.config.value
        drawContext.withTranslate(x.toFloat(), y.toFloat()) {
            layoutConfig.value.forEach { widgetConfig ->
                val drawQueue = DrawQueue()
                val context = Context(
                    drawQueue = drawQueue,
                    size = IntSize(width, height),
                    screenOffset = IntOffset(0, 0),
                    scale = client.window.scaleFactor.toFloat(),
                    pointers = mutableMapOf(),
                    config = config,
                    opacity = widgetConfig.opacity,
                    designMode = true,
                    result = ContextResult()
                )
                context.transformDrawQueue(
                    drawTransform = { draw ->
                        withBlend {
                            withBlendFunction(
                                srcFactor = GlStateManager.SrcFactor.SRC_ALPHA,
                                dstFactor = GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
                                srcAlpha = GlStateManager.SrcFactor.ONE,
                                dstAlpha = GlStateManager.DstFactor.ZERO
                            ) {
                                draw()
                            }
                        }
                    }
                ) {
                    withAlign(
                        align = widgetConfig.align,
                        offset = widgetConfig.offset,
                        size = widgetConfig.size()
                    ) {
                        widgetConfig.layout(this)
                        if (selectedConfig.value == widgetConfig) {
                            this.drawQueue.enqueue { drawContext, _ ->
                                drawContext.drawBorder(0, 0, size.width, size.height, Colors.WHITE)
                            }
                        }
                    }
                }
                drawQueue.execute(drawContext, client.textRenderer)
            }
        }
    }


    private var dragStart = Offset.ZERO
    private var origOffset = IntOffset.ZERO

    override fun onClick(mouseX: Double, mouseY: Double) {
        val pointerPosition = IntOffset(x = mouseX.toInt() - x, y = mouseY.toInt() - y)
        val screenSize = IntSize(width, height)

        for (widget in layoutConfig.value) {
            val size = widget.size()
            val offset = widget.align.alignOffset(screenSize, size, widget.offset)
            val pointerOffset = pointerPosition - offset
            if (pointerOffset in size) {
                dragStart = Offset(mouseX.toFloat(), mouseY.toFloat())
                origOffset = widget.offset
                client.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
                selectedConfig.value = widget
                return
            }
        }
        selectedConfig.value = null
    }

    override fun onDrag(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double) {
        val config = selectedConfig.value ?: return
        val dragPosition = Offset(mouseX.toFloat(), mouseY.toFloat()) - dragStart
        val newOffset = when (config.align) {
            LEFT_TOP, LEFT_CENTER, CENTER_TOP, CENTER_CENTER -> IntOffset(
                x = origOffset.x + dragPosition.x.toInt(),
                y = origOffset.y + dragPosition.y.toInt()
            )

            RIGHT_TOP, RIGHT_CENTER -> IntOffset(
                x = origOffset.x - dragPosition.x.toInt(),
                y = origOffset.y + dragPosition.y.toInt()
            )

            LEFT_BOTTOM, CENTER_BOTTOM -> IntOffset(
                x = origOffset.x + dragPosition.x.toInt(),
                y = origOffset.y - dragPosition.y.toInt()
            )

            RIGHT_BOTTOM -> IntOffset(
                x = origOffset.x - dragPosition.x.toInt(),
                y = origOffset.y - dragPosition.y.toInt()
            )
        }
        val newConfig = config.cloneBase(offset = newOffset)
        layoutConfig.replaceItem(config, newConfig)
        selectedConfig.value = newConfig
    }

    override fun playDownSound(soundManager: SoundManager) {}

    override fun appendClickableNarrations(builder: NarrationMessageBuilder) {}
}
