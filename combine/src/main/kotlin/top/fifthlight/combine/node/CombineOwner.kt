package top.fifthlight.combine.node

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.*
import top.fifthlight.combine.input.PointerEvent
import top.fifthlight.combine.input.PointerEventReceiver
import top.fifthlight.combine.input.PointerEventType
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
) : CoroutineScope, PointerEventReceiver {
    private val clock = BroadcastFrameClock()
    private val composeScope = CoroutineScope(dispatcher) + clock
    override val coroutineContext: CoroutineContext = composeScope.coroutineContext

    private var running = false
    private val recomposer = Recomposer(coroutineContext)
    private val layers = mutableListOf(LayoutNode().let { rootNode ->
        Layer(
            owner = this,
            rootNode = rootNode,
            composition = Composition(UiApplier(rootNode), recomposer),
        )
    })
    private val rootLayer
        get() = layers.first()

    private data class Layer(
        val owner: CombineOwner,
        val rootNode: LayoutNode,
        val composition: Composition,
        val parentContext: CompositionContext? = null,
        val onDismissRequest: (() -> Unit)? = null,
    ) : DisposableLayer {
        override fun dispose() {
            owner.layers.remove(this)
            composition.dispose()
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

    fun setContent(content: @Composable () -> Unit) {
        rootLayer.composition.setContent {
            CompositionLocalProvider(
                LocalCombineOwner provides this,
                LocalTextMeasurer provides textMeasurer,
            ) {
                content()
            }
        }
    }

    fun addLayer(
        parentContext: CompositionContext,
        onDismissRequest: (() -> Unit)? = null,
        content: @Composable () -> Unit,
    ): DisposableLayer {
        val rootNode = LayoutNode()
        val composition = Composition(UiApplier(rootNode), parentContext)
        composition.setContent(content)
        val layer = Layer(
            owner = this,
            rootNode = rootNode,
            composition = composition,
            parentContext = parentContext,
            onDismissRequest = onDismissRequest,
        )
        layers.add(layer)
        return layer
    }

    override fun onPointerEvent(event: PointerEvent): Boolean {
        val layer = layers.last()
        if (layer.rootNode.onPointerEvent(event)) {
            return true
        }
        if (layers.size == 1) {
            return false
        } else if (event.type == PointerEventType.Press) {
            layer.onDismissRequest?.let { it() }
        }
        return false
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