package top.fifthlight.touchcontroller.ui.component.config.layout

import androidx.compose.runtime.*
import org.koin.compose.koinInject
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.onPlaced
import top.fifthlight.combine.modifier.placement.size
import top.fifthlight.combine.paint.*
import top.fifthlight.combine.widget.base.Canvas
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.config.GlobalConfig
import top.fifthlight.touchcontroller.control.ControllerWidget
import top.fifthlight.touchcontroller.gal.DefaultItemListProvider
import top.fifthlight.touchcontroller.layout.Context
import top.fifthlight.touchcontroller.layout.ContextResult
import top.fifthlight.touchcontroller.layout.DrawQueue

@Composable
fun ControllerWidget(
    modifier: Modifier = Modifier,
    config: ControllerWidget,
) {
    val itemListProvider: DefaultItemListProvider = koinInject()
    val drawQueue = remember(config, itemListProvider) {
        val queue = DrawQueue()
        val context = Context(
            windowSize = IntSize.ZERO,
            windowScaledSize = IntSize.ZERO,
            drawQueue = queue,
            size = config.size(),
            screenOffset = IntOffset.ZERO,
            pointers = mutableMapOf(),
            result = ContextResult(),
            config = GlobalConfig.default(itemListProvider),
            opacity = config.opacity,
        )
        config.layout(context)
        queue
    }
    Canvas(
        modifier = Modifier
            .size(config.size())
            .then(modifier)
    ) {
        canvas.withBlend {
            withBlendFunction(
                BlendFunction(
                    srcFactor = BlendFactor.SRC_ALPHA,
                    dstFactor = BlendFactor.ONE_MINUS_SRC_ALPHA,
                    srcAlpha = BlendFactor.ONE,
                    dstAlpha = BlendFactor.ZERO,
                )
            ) {
                drawQueue.execute(canvas)
            }
        }
    }
}

@Composable
fun ScaledControllerWidget(
    modifier: Modifier = Modifier,
    config: ControllerWidget,
) {
    var entrySize by remember { mutableStateOf(IntSize.ZERO) }
    val itemListProvider: DefaultItemListProvider = koinInject()
    val (drawQueue, componentScaleFactor, offset) = remember(config, entrySize, itemListProvider) {
        val queue = DrawQueue()

        val widgetSize = config.size()
        val widthFactor = if (widgetSize.width > entrySize.width) {
            entrySize.width.toFloat() / widgetSize.width.toFloat()
        } else 1f
        val heightFactor = if (widgetSize.height > entrySize.height) {
            entrySize.height.toFloat() / widgetSize.height.toFloat()
        } else 1f
        val componentScaleFactor = widthFactor.coerceAtMost(heightFactor)
        val displaySize = (widgetSize.toSize() * componentScaleFactor).toIntSize()
        val offset = (entrySize - displaySize) / 2

        val context = Context(
            windowSize = IntSize.ZERO,
            windowScaledSize = IntSize.ZERO,
            drawQueue = queue,
            size = config.size(),
            screenOffset = IntOffset.ZERO,
            pointers = mutableMapOf(),
            result = ContextResult(),
            config = GlobalConfig.default(itemListProvider),
            opacity = config.opacity,
        )
        config.layout(context)
        Triple(queue, componentScaleFactor, offset)
    }
    Canvas(
        modifier = Modifier
            .onPlaced { entrySize = it.size }
            .then(modifier),
    ) {
        canvas.withTranslate(offset) {
            canvas.withScale(componentScaleFactor) {
                withBlend {
                    withBlendFunction(
                        BlendFunction(
                            srcFactor = BlendFactor.SRC_ALPHA,
                            dstFactor = BlendFactor.ONE_MINUS_SRC_ALPHA,
                            srcAlpha = BlendFactor.ONE,
                            dstAlpha = BlendFactor.ZERO,
                        )
                    ) {
                        drawQueue.execute(canvas)
                    }
                }
            }
        }
    }
}