package top.fifthlight.combine.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.Stable
import androidx.compose.runtime.currentComposer
import top.fifthlight.combine.modifier.Constraints
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.node.LayoutNode
import top.fifthlight.combine.node.UiApplier
import top.fifthlight.combine.paint.NodeRenderer

@Composable
inline fun Layout(
    measurePolicy: MeasurePolicy,
    modifier: Modifier = Modifier,
    renderer: NodeRenderer = NodeRenderer.EmptyRenderer,
    content: @Composable () -> Unit = {}
) {
    val localMap = currentComposer.currentCompositionLocalMap

    ComposeNode<LayoutNode, UiApplier>(
        factory = ::LayoutNode,
        update = {
            set(measurePolicy) { this.measurePolicy = it }
            set(renderer) { this.renderer = it }
            set(modifier) { this.modifier = it }
            set(localMap) { this.compositionLocalMap = it }
        },
        content = content,
    )
}

@Stable
fun Constraints.offset(horizontal: Int = 0, vertical: Int = 0) = Constraints(
    (minWidth + horizontal).coerceAtLeast(0),
    addMaxWithMinimum(maxWidth, horizontal),
    (minHeight + vertical).coerceAtLeast(0),
    addMaxWithMinimum(maxHeight, vertical)
)

private fun addMaxWithMinimum(max: Int, value: Int): Int {
    return if (max == Int.MAX_VALUE) {
        max
    } else {
        (max + value).coerceAtLeast(0)
    }
}
