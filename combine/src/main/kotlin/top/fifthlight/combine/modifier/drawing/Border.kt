package top.fifthlight.combine.modifier.drawing

import top.fifthlight.combine.layout.Measurable
import top.fifthlight.combine.layout.MeasureResult
import top.fifthlight.combine.layout.MeasureScope
import top.fifthlight.combine.layout.Placeable
import top.fifthlight.combine.modifier.*
import top.fifthlight.combine.paint.Color
import top.fifthlight.combine.paint.RenderContext
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize

fun Modifier.border(size: Int = 0, color: Color): Modifier = border(size, size, color)

fun Modifier.border(width: Int = 0, height: Int = 0, color: Color): Modifier = border(width, height, width, height, color)

fun Modifier.border(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0, color: Color): Modifier =
    then(BorderNode(left, top, right, bottom, color))

private data class BorderNode(
    val left: Int = 0,
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0,
    val color: Color,
) : DrawModifierNode, LayoutModifierNode, Modifier.Node<BorderNode> {
    override fun renderAfterContext(context: RenderContext, node: Placeable) {
        if (left > 0) {
            context.canvas.fillRect(
                offset = IntOffset(0, 0),
                size = IntSize(left, node.height),
                color = color
            )
        }
        if (top > 0) {
            context.canvas.fillRect(
                offset = IntOffset(0, 0),
                size = IntSize(node.width, top),
                color = color
            )
        }
        if (right > 0) {
            context.canvas.fillRect(
                offset = IntOffset(node.width - right, 0),
                size = IntSize(right, node.height),
                color = color
            )
        }
        if (bottom > 0) {
            context.canvas.fillRect(
                offset = IntOffset(0, node.height - bottom),
                size = IntSize(node.width, bottom),
                color = color
            )
        }
    }

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val horizontalBorder = left + right
        val verticalBorder = top + bottom
        val adjustedConstraints = constraints.offset(-horizontalBorder, -verticalBorder)

        val placeable = measurable.measure(adjustedConstraints)
        val width = (placeable.width + horizontalBorder).coerceIn(constraints.minWidth, constraints.maxWidth)
        val height = (placeable.height + verticalBorder).coerceIn(constraints.minHeight, constraints.maxHeight)

        return layout(width, height) {
            placeable.placeAt(left, top)
        }
    }
}

