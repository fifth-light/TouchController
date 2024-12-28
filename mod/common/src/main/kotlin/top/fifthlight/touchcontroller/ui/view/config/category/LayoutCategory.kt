package top.fifthlight.touchcontroller.ui.view.config.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.collections.immutable.persistentMapOf
import top.fifthlight.combine.layout.Alignment
import top.fifthlight.combine.layout.Arrangement
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.drawing.border
import top.fifthlight.combine.modifier.placement.*
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.util.LocalCloseHandler
import top.fifthlight.combine.widget.base.Canvas
import top.fifthlight.combine.widget.base.Text
import top.fifthlight.combine.widget.base.layout.*
import top.fifthlight.combine.widget.ui.Button
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.config.LayoutLayer
import top.fifthlight.touchcontroller.config.TouchControllerConfig
import top.fifthlight.touchcontroller.control.ControllerWidget
import top.fifthlight.touchcontroller.layout.Align
import top.fifthlight.touchcontroller.layout.Context
import top.fifthlight.touchcontroller.layout.ContextResult
import top.fifthlight.touchcontroller.layout.DrawQueue
import top.fifthlight.touchcontroller.ui.state.LayoutPanelState

@Composable
private fun BoxScope.ControllerWidget(config: ControllerWidget) {
    var modifier: Modifier = Modifier.size(config.size())
    modifier = when (config.align) {
        Align.LEFT_TOP -> modifier
            .alignment(Alignment.TopLeft)
            .offset(x = config.offset.x, y = config.offset.y)

        Align.CENTER_TOP -> modifier
            .alignment(Alignment.TopCenter)
            .offset(x = config.offset.x, y = config.offset.y)

        Align.RIGHT_TOP -> modifier
            .alignment(Alignment.TopRight)
            .offset(x = -config.offset.x, y = config.offset.y)

        Align.LEFT_CENTER -> modifier
            .alignment(Alignment.CenterLeft)
            .offset(x = config.offset.x, y = config.offset.y)

        Align.CENTER_CENTER -> modifier
            .alignment(Alignment.Center)
            .offset(x = config.offset.x, y = config.offset.y)

        Align.RIGHT_CENTER -> modifier
            .alignment(Alignment.CenterRight)
            .offset(x = -config.offset.x, y = config.offset.y)

        Align.LEFT_BOTTOM -> modifier
            .alignment(Alignment.BottomLeft)
            .offset(x = config.offset.x, y = -config.offset.y)

        Align.CENTER_BOTTOM -> modifier
            .alignment(Alignment.BottomCenter)
            .offset(x = config.offset.x, y = -config.offset.y)

        Align.RIGHT_BOTTOM -> modifier
            .alignment(Alignment.BottomRight)
            .offset(x = -config.offset.x, y = -config.offset.y)
    }
    val drawQueue = DrawQueue()
    val context = Context(
        windowSize = IntSize.ZERO,
        windowScaledSize = IntSize.ZERO,
        drawQueue = drawQueue,
        size = config.size(),
        screenOffset = IntOffset.ZERO,
        pointers = mutableMapOf(),
        result = ContextResult(),
        config = TouchControllerConfig(),
        condition = persistentMapOf(),
    )
    config.layout(context)
    Canvas(modifier) {
        drawQueue.execute(canvas)
    }
}

@Composable
private fun WidgetsPanel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        alignment = Alignment.Center,
    ) {
        Text("TODO")
    }
}

@Composable
private fun LayersPanel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        alignment = Alignment.Center,
    ) {
        Text("TODO")
    }
}

@Composable
private fun PresetsPanel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        alignment = Alignment.Center,
    ) {
        Text("TODO")
    }
}

@Composable
private fun LayoutEditorPanel(
    modifier: Modifier = Modifier,
    layer: LayoutLayer = LayoutLayer(),
    onLayerChanged: (LayoutLayer) -> Unit = {},
) {
    Box(modifier) {
        for (widget in layer.widgets) {
            ControllerWidget(widget)
        }
    }
}

data object LayoutCategory : ConfigCategory(
    title = Texts.SCREEN_OPTIONS_CATEGORY_CUSTOM_TITLE,
    content = { modifier, viewModel ->
        val uiState by viewModel.uiState.collectAsState()

        Column(modifier) {
            Row(
                modifier = Modifier
                    .padding(4)
                    .fillMaxWidth()
                    .height(32)
                    .border(bottom = 1, color = Colors.WHITE),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4),
            ) {
                Button(onClick = {
                    viewModel.toggleWidgetsPanel()
                }) {
                    Text("Add widget", shadow = true)
                }
                Button(onClick = {
                    viewModel.toggleLayersPanel()
                }) {
                    Text("Layers", shadow = true)
                }
                Button(onClick = {
                    viewModel.togglePresetsPanel()
                }) {
                    Text("Presets", shadow = true)
                }

                Spacer(modifier = Modifier.weight(1f))

                val closeHandler = LocalCloseHandler.current
                Button(onClick = {
                    viewModel.reset()
                }) {
                    Text("Reset", shadow = true)
                }
                Button(onClick = {
                    viewModel.exit(closeHandler)
                }) {
                    Text("Cancel", shadow = true)
                }
                Button(onClick = {
                    viewModel.saveAndExit(closeHandler)
                }) {
                    Text("Save", shadow = true)
                }
            }
            when (uiState.layoutPanelState) {
                LayoutPanelState.LAYOUT -> run panel@{
                    val selectedLayer = uiState.selectedLayer
                    val currentLayer = uiState.layout.getOrNull(selectedLayer) ?: run {
                        Box(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            alignment = Alignment.Center,
                        ) {
                            Text("No layer selected")
                        }
                        return@panel
                    }
                    LayoutEditorPanel(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        layer = currentLayer,
                        onLayerChanged = {
                            viewModel.updateLayer(selectedLayer, it)
                        }
                    )
                }

                LayoutPanelState.LAYERS -> LayersPanel(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                LayoutPanelState.WIDGETS -> WidgetsPanel(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                LayoutPanelState.PRESETS -> PresetsPanel(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
)