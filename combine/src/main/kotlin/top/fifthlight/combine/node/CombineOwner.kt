package top.fifthlight.combine.node

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.*
import top.fifthlight.combine.input.focus.FocusManager
import top.fifthlight.combine.input.focus.LocalFocusManager
import top.fifthlight.combine.input.input.TextInputReceiver
import top.fifthlight.combine.input.key.KeyEvent
import top.fifthlight.combine.input.key.KeyEventReceiver
import top.fifthlight.combine.input.pointer.PointerEvent
import top.fifthlight.combine.input.pointer.PointerEventReceiver
import top.fifthlight.combine.input.pointer.PointerEventType
import top.fifthlight.combine.modifier.Constraints
import top.fifthlight.combine.paint.RenderContext
import top.fifthlight.combine.paint.TextMeasurer
import top.fifthlight.data.IntSize
import kotlin.coroutines.CoroutineContext

val LocalCombineOwner: ProvidableCompositionLocal<CombineOwner> =
    staticCompositionLocalOf { error("No CombineOwner in context") }
val LocalTextMeasurer: ProvidableCompositionLocal<TextMeasurer> =
    staticCompositionLocalOf { error("No TextMeasurer in context") }

abstract class CombineCoroutineDispatcher : CoroutineDispatcher() {
    abstract fun execute()
}

interface DisposableLayer {
    fun dispose()
}

class CombineOwner(
    private val dispatcher: CombineCoroutineDispatcher,
    private val textMeasurer: TextMeasurer
) : CoroutineScope, PointerEventReceiver, TextInputReceiver, KeyEventReceiver {
    private val clock = BroadcastFrameClock()
    private val composeScope = CoroutineScope(dispatcher) + clock
    override val coroutineContext: CoroutineContext = composeScope.coroutineContext

    private var running = false
    private val recomposer = Recomposer(coroutineContext)
    private val layers = mutableListOf(LayoutNode().let { rootNode ->
        Layer(
            owner = this,
            rootNode = rootNode,
            focusManager = FocusManager(),
            composition = Composition(UiApplier(rootNode), recomposer),
        )
    })
    private val rootLayer
        get() = layers.first()

    private data class Layer(
        val owner: CombineOwner,
        val rootNode: LayoutNode,
        val focusManager: FocusManager,
        val composition: Composition,
        val parentContext: CompositionContext? = null,
        val onDismissRequest: (() -> Unit)? = null,
    ) : DisposableLayer {
        override fun dispose() {
            owner.layers.remove(this)
            composition.dispose()
        }

        fun setContent(content: @Composable () -> Unit) {
            composition.setContent {
                CompositionLocalProvider(
                    LocalCombineOwner provides owner,
                    LocalTextMeasurer provides owner.textMeasurer,
                    LocalFocusManager provides focusManager,
                ) {
                    content()
                }
            }
        }
    }

    private var applyScheduled = false
    private val snapshotHandle = Snapshot.registerGlobalWriteObserver {
        if (!applyScheduled) {
            applyScheduled = true
            composeScope.launch {
                applyScheduled = false
                Snapshot.sendApplyNotifications()
            }
        }
    }

    fun start() {
        if (running) {
            return
        }
        running = true
        launch {
            recomposer.runRecomposeAndApplyChanges()
        }
    }

    fun setContent(content: @Composable () -> Unit) = rootLayer.setContent(content)

    fun addLayer(
        parentContext: CompositionContext,
        onDismissRequest: (() -> Unit)? = null,
        content: @Composable () -> Unit,
    ): DisposableLayer {
        val rootNode = LayoutNode()
        val layer = Layer(
            owner = this,
            rootNode = rootNode,
            focusManager = FocusManager(),
            composition = Composition(UiApplier(rootNode), parentContext),
            parentContext = parentContext,
            onDismissRequest = onDismissRequest,
        )
        layer.setContent(content)
        layers.add(layer)
        return layer
    }

    override fun onPointerEvent(event: PointerEvent): Boolean {
        val layer = layers.last()
        if (layer.rootNode.onPointerEvent(event)) {
            return true
        }
        if (event.type == PointerEventType.Press) {
            layer.focusManager.requestBlur()
        }
        if (layers.size == 1) {
            return false
        } else if (event.type == PointerEventType.Press) {
            layer.onDismissRequest?.let { it() }
        }
        return false
    }

    override fun onTextInput(string: String) {
        val layer = layers.last()
        val focusedNode = layer.focusManager.focusedNode.value ?: return
        focusedNode.onTextInput(string)
    }

    override fun onKeyEvent(event: KeyEvent) {
        val layer = layers.last()
        val focusedNode = layer.focusManager.focusedNode.value ?: return
        focusedNode.onKeyEvent(event)
    }

    fun render(size: IntSize, context: RenderContext) {
        clock.sendFrame(System.nanoTime())
        dispatcher.execute()
        for (layer in layers) {
            layer.rootNode.measure(
                Constraints(
                    maxWidth = size.width,
                    maxHeight = size.height
                )
            )
            layer.rootNode.render(context)
        }
    }

    fun close() {
        recomposer.close()
        snapshotHandle.dispose()
        for (layer in layers) {
            layer.composition.dispose()
        }
        composeScope.cancel()
    }
}