package top.fifthlight.touchcontroller.ui.component.config.layout

import androidx.compose.runtime.*
import top.fifthlight.combine.layout.Layout
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.ParentDataModifierNode
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.drawing.innerLine
import top.fifthlight.combine.modifier.placement.fillMaxHeight
import top.fifthlight.combine.modifier.placement.padding
import top.fifthlight.combine.modifier.placement.width
import top.fifthlight.combine.modifier.pointer.clickable
import top.fifthlight.combine.modifier.pointer.consumePress
import top.fifthlight.combine.modifier.pointer.draggable
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.widget.base.layout.Row
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize
import top.fifthlight.data.Offset
import top.fifthlight.touchcontroller.config.LayoutLayer
import top.fifthlight.touchcontroller.control.ControllerWidget
import top.fifthlight.touchcontroller.layout.Align

private data class ControllerWidgetParentData(
    val align: Align,
    val offset: IntOffset,
    val size: IntSize,
)

private data class ControllerWidgetModifierNode(
    val align: Align,
    val offset: IntOffset,
    val size: IntSize,
) : ParentDataModifierNode, Modifier.Node<ControllerWidgetModifierNode> {
    constructor(widget: ControllerWidget) : this(widget.align, widget.offset, widget.size())

    override fun modifierParentData(parentData: Any?): ControllerWidgetParentData {
        return ControllerWidgetParentData(
            align = align,
            offset = offset,
            size = size
        )
    }
}

@Composable
fun LayoutEditorPanel(
    modifier: Modifier = Modifier,
    selectedWidgetIndex: Int = -1,
    onSelectedWidgetChanged: (Int, ControllerWidget?) -> Unit = { _, _ -> },
    layer: LayoutLayer,
    layerIndex: Int,
    onLayerChanged: (LayoutLayer) -> Unit = {},
    onWidgetCopied: (ControllerWidget) -> Unit = { _ -> },
) {
    val selectedWidget = layer.widgets.getOrNull(selectedWidgetIndex)
    var panelSize by remember { mutableStateOf(IntSize.ZERO) }
    Row(modifier) {
        Layout(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .consumePress {
                    onSelectedWidgetChanged(-1, null)
                },
            measurePolicy = { measurables, constraints ->
                val childConstraint = constraints.copy(minWidth = 0, minHeight = 0)
                val placeables = measurables.map { it.measure(childConstraint) }

                val width = constraints.maxWidth
                val height = constraints.maxHeight
                panelSize = IntSize(width, height)
                layout(width, height) {
                    placeables.forEachIndexed { index, placeable ->
                        val parentData = measurables[index].parentData as ControllerWidgetParentData
                        placeable.placeAt(
                            parentData.align.alignOffset(
                                windowSize = IntSize(width, height),
                                size = parentData.size,
                                offset = parentData.offset
                            )
                        )
                    }
                }
            }
        ) {
            for ((index, widget) in layer.widgets.withIndex()) {
                if (selectedWidgetIndex == index) {
                    var dragTotalOffset by remember { mutableStateOf(Offset.ZERO) }
                    var widgetInitialOffset by remember { mutableStateOf(IntOffset.ZERO) }
                    LaunchedEffect(selectedWidgetIndex, layerIndex, selectedWidget?.align) {
                        widgetInitialOffset = layer.widgets.getOrNull(selectedWidgetIndex)?.offset ?: IntOffset.ZERO
                        dragTotalOffset = Offset.ZERO
                    }
                    ControllerWidget(
                        modifier = Modifier
                            .then(ControllerWidgetModifierNode(widget))
                            .innerLine(Colors.WHITE)
                            .draggable { offset ->
                                dragTotalOffset += offset
                                val intOffset = dragTotalOffset.toIntOffset()
                                val appliedOffset = when (widget.align) {
                                    Align.LEFT_TOP, Align.CENTER_TOP, Align.LEFT_CENTER, Align.CENTER_CENTER -> intOffset
                                    Align.RIGHT_TOP, Align.RIGHT_CENTER -> IntOffset(-intOffset.x, intOffset.y)
                                    Align.LEFT_BOTTOM, Align.CENTER_BOTTOM -> IntOffset(intOffset.x, -intOffset.y)
                                    Align.RIGHT_BOTTOM -> -intOffset
                                }
                                val widgetOffset = widgetInitialOffset + appliedOffset
                                val widgetSize = widget.size()
                                val clampedOffset = IntOffset(
                                    x = when (widget.align) {
                                        Align.LEFT_TOP, Align.LEFT_CENTER, Align.LEFT_BOTTOM ->
                                            widgetOffset.x.coerceIn(0, panelSize.width - widgetSize.width)

                                        Align.CENTER_CENTER, Align.CENTER_BOTTOM, Align.CENTER_TOP ->
                                            widgetOffset.x.coerceIn(
                                                -panelSize.width / 2 + widgetSize.width / 2,
                                                panelSize.width / 2 - widgetSize.width / 2
                                            )

                                        Align.RIGHT_TOP, Align.RIGHT_CENTER, Align.RIGHT_BOTTOM ->
                                            widgetOffset.x.coerceIn(0, panelSize.width - widgetSize.width)
                                    },
                                    y = when (widget.align) {
                                        Align.LEFT_TOP, Align.CENTER_TOP, Align.RIGHT_TOP ->
                                            widgetOffset.y.coerceIn(0, panelSize.height - widgetSize.height)

                                        Align.LEFT_CENTER, Align.CENTER_CENTER, Align.RIGHT_CENTER ->
                                            widgetOffset.y.coerceIn(
                                                -panelSize.height / 2 + widgetSize.height / 2,
                                                panelSize.height / 2 - widgetSize.height / 2
                                            )

                                        Align.LEFT_BOTTOM, Align.CENTER_BOTTOM, Align.RIGHT_BOTTOM ->
                                            widgetOffset.y.coerceIn(0, panelSize.height - widgetSize.height)
                                    }
                                )
                                val newWidget = widget.cloneBase(
                                    offset = clampedOffset,
                                )
                                onLayerChanged(
                                    layer.copy(
                                        widgets = layer.widgets.set(index, newWidget)
                                    )
                                )
                            },
                        config = widget
                    )
                } else {
                    ControllerWidget(
                        modifier = Modifier
                            .then(ControllerWidgetModifierNode(widget))
                            .clickable {
                                onSelectedWidgetChanged(index, widget)
                            },
                        config = widget
                    )
                }
            }
        }
        if (selectedWidget != null) {
            WidgetProperties(
                modifier = Modifier
                    .padding(4)
                    .fillMaxHeight()
                    .border(left = 1, color = Colors.WHITE)
                    .width(128),
                widget = selectedWidget,
                onWidgetRemoved = {
                    onSelectedWidgetChanged(-1, null)
                    onLayerChanged(
                        layer.copy(
                            widgets = layer.widgets.removeAt(selectedWidgetIndex),
                        )
                    )
                },
                onWidgetCopied = {
                    onWidgetCopied(selectedWidget)
                },
                onWidgetCut = {
                    onWidgetCopied(selectedWidget)
                    onSelectedWidgetChanged(-1, null)
                    onLayerChanged(
                        layer.copy(
                            widgets = layer.widgets.removeAt(selectedWidgetIndex),
                        )
                    )
                },
                onPropertyChanged = {
                    onLayerChanged(
                        layer.copy(
                            widgets = layer.widgets.set(selectedWidgetIndex, it)
                        )
                    )
                }
            )
        }
    }
}