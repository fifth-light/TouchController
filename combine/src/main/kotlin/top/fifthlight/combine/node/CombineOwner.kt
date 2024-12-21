package top.fifthlight.combine.node

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.*
import top.fifthlight.combine.input.PointerEvent
import top.fifthlight.combine.input.PointerEventReceiver
import top.fifthlight.combine.modifier.Constraints
import top.fifthlight.combine.paint.RenderContext
import top.fifthlight.combine.paint.TextMeasurer
import top.fifthlight.data.IntSize
import kotlin.coroutines.CoroutineContext

val LocalCombineOwner: ProvidableCompositionLocal<CombineOwner> = staticCompositionLocalOf { error("No CombineOwner in context") }
val LocalTextMeasurer: ProvidableCompositionLocal<TextMeasurer> = staticCompositionLocalOf { error("No TextMeasurer in context") }

abstract class CombineCoroutineDispatcher: CoroutineDispatcher() {
    abstract fun execute()
}

class CombineOwner(
    private val dispatcher: CombineCoroutineDispatcher,
    private val textMeasurer: TextMeasurer
) : CoroutineScope, PointerEventReceiver {
    private val clock = BroadcastFrameClock()
    private val composeScope = CoroutineScope(dispatcher) + clock
    override val coroutineContext: CoroutineContext = composeScope.coroutineContext

    private var running = false
    private val rootNode = LayoutNode()
    private val recomposer = Recomposer(coroutineContext)
    private val composition = Composition(UiApplier(rootNode), recomposer)

    var applyScheduled = false
    val snapshotHandle = Snapshot.registerGlobalWriteObserver {
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
        composition.setContent {
            CompositionLocalProvider(LocalTextMeasurer provides textMeasurer) {
                content()
            }
        }
    }

    override fun onPointerEvent(event: PointerEvent) = rootNode.onPointerEvent(event)

    fun render(size: IntSize, context: RenderContext) {
        clock.sendFrame(System.nanoTime())
        dispatcher.execute()
        rootNode.measure(Constraints(
            maxWidth = size.width,
            maxHeight = size.height
        ))
        rootNode.render(context)
    }

    fun close() {
        recomposer.close()
        snapshotHandle.dispose()
        composition.dispose()
        composeScope.cancel()
    }
}