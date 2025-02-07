package top.fifthlight.combine.modifier.scroll

import androidx.compose.runtime.Composable
import top.fifthlight.combine.input.pointer.PointerEvent
import top.fifthlight.combine.input.pointer.PointerEventType
import top.fifthlight.combine.layout.Measurable
import top.fifthlight.combine.layout.MeasureResult
import top.fifthlight.combine.layout.MeasureScope
import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.*
import top.fifthlight.combine.node.LayoutNode
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.paint.RenderContext
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntRect
import top.fifthlight.data.IntSize
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun Modifier.verticalScroll(
    scrollState: ScrollState = rememberScrollState(),
) = then(VerticalScrollNode(scrollState))

private data class VerticalScrollNode(
    val scrollState: ScrollState,
) : LayoutModifierNode, DrawModifierNode, PointerInputModifierNode, Modifier.Node<VerticalScrollNode> {
    override fun onPointerEvent(
        event: PointerEvent,
        node: Placeable,
        layoutNode: LayoutNode,
        children: (PointerEvent) -> Boolean
    ): Boolean {
        return when (event.type) {
            PointerEventType.Scroll -> {
                scrollState.updateProgress((scrollState.progress.value - event.scrollDelta.y * 12).toInt())
                true
            }

            PointerEventType.Press -> {
                scrollState.initialPointerPosition = event.position
                scrollState.startPointerPosition = null
                scrollState.scrolling = false
                false
            }

            PointerEventType.Cancel, PointerEventType.Release -> {
                scrollState.initialPointerPosition = null
                scrollState.startPointerPosition = null
                if (scrollState.scrolling) {
                    scrollState.scrolling = false
                    true
                } else {
                    false
                }
            }

            PointerEventType.Move -> {
                val initialPosition = scrollState.initialPointerPosition
                if (scrollState.scrolling) {
                    val distance = (scrollState.startPointerPosition!!.y - event.position.y).roundToInt()
                    scrollState.updateProgress(distance + scrollState.startProgress)
                    true
                } else if (initialPosition != null) {
                    val distance = (initialPosition.y - event.position.y)
                    if (distance.absoluteValue > 8) {
                        scrollState.scrolling = true
                        scrollState.startProgress = scrollState.progress.value
                        scrollState.startPointerPosition = event.position
                        children(event.copy(type = PointerEventType.Cancel))
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            }

            else -> false
        }
    }

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val viewportMaxHeight = constraints.maxHeight
        if (viewportMaxHeight == Int.MAX_VALUE) {
            error("Bad maxHeight of verticalScroll(): check whether you nested two verticalScroll() modifier")
        }

        val placeable = measurable.measure(
            constraints.copy(
                minWidth = constraints.minWidth,
                maxWidth = constraints.maxWidth,
                minHeight = constraints.minHeight,
                maxHeight = Int.MAX_VALUE,
            )
        )

        val viewportHeight = placeable.height.coerceAtMost(viewportMaxHeight)
        scrollState.contentHeight = placeable.height
        scrollState.viewportHeight = viewportHeight

        val progress = scrollState.progress.value
        if (progress + viewportHeight > placeable.height) {
            scrollState.updateProgress(placeable.height - viewportHeight)
        }

        return layout(placeable.width, viewportHeight) {
            placeable.placeAt(0, -progress)
        }
    }

    override fun RenderContext.renderBefore(node: Placeable) {
        with(canvas) {
            pushClip(
                IntRect(
                    offset = IntOffset(node.absoluteX, node.absoluteY),
                    size = IntSize(node.width, node.height)
                ),
                IntRect(
                    offset = IntOffset(node.x, node.y),
                    size = IntSize(node.width, node.height)
                ),
            )
        }
    }

    override fun RenderContext.renderAfter(node: Placeable) {
        with(canvas) {
            if (scrollState.viewportHeight < scrollState.contentHeight) {
                val progress =
                    scrollState.progress.value.toFloat() / (scrollState.contentHeight - scrollState.viewportHeight).toFloat()
                val barHeight = (node.height * scrollState.viewportHeight / scrollState.contentHeight).coerceAtLeast(12)
                val barY = ((node.height - barHeight) * progress).roundToInt()
                fillRect(
                    offset = IntOffset(node.width - 3, barY),
                    size = IntSize(3, barHeight),
                    color = Colors.ALTERNATE_WHITE,
                )
            }
            popClip()
        }
    }
}
