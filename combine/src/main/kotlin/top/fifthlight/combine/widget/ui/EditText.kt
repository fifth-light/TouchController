package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.input.MutableInteractionSource
import top.fifthlight.combine.input.input.LocalClipboard
import top.fifthlight.combine.input.input.TextInputState
import top.fifthlight.combine.input.key.Key
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.focus.FocusInteraction
import top.fifthlight.combine.modifier.focus.focusable
import top.fifthlight.combine.modifier.input.textInput
import top.fifthlight.combine.modifier.key.onKeyEvent
import top.fifthlight.combine.modifier.placement.minHeight
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.widget.base.Canvas
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize

@Composable
fun EditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    placeholder: Text? = null,
) {
    val clipboard = LocalClipboard.current
    val interactionSource = remember { MutableInteractionSource() }
    var focused by remember { mutableStateOf(false) }
    var cursorShow by remember { mutableStateOf(false) }
    var textInputState by remember { mutableStateOf(TextInputState(value)) }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect {
            when (it) {
                FocusInteraction.Blur -> focused = false
                FocusInteraction.Focus -> focused = true
            }
        }
    }
    LaunchedEffect(value) {
        if (value == textInputState.text) {
            return@LaunchedEffect
        }
        textInputState = TextInputState(value)
    }
    LaunchedEffect(focused) {
        if (focused) {
            while (true) {
                cursorShow = !cursorShow
                delay(500)
            }
        } else {
            cursorShow = false
        }
    }

    fun updateInputState(block: TextInputState.() -> TextInputState) {
        textInputState = block(textInputState)
        onValueChanged(textInputState.text)
    }

    Canvas(
        modifier = Modifier
            .minHeight(20)
            .focusable(interactionSource)
            .textInput { updateInputState { commitText(it) } }
            .onKeyEvent { event ->
                if (!event.pressed) {
                    return@onKeyEvent
                }
                when (event.key) {
                    Key.DELETE -> updateInputState { doDelete() }
                    Key.BACKSPACE -> updateInputState { doBackspace() }

                    Key.HOME -> if (event.modifier.onlyShift) {
                        updateInputState { doShiftHome() }
                    } else if (event.modifier.empty) {
                        updateInputState { doHome() }
                    }

                    Key.END -> if (event.modifier.onlyShift) {
                        updateInputState { doShiftEnd() }
                    } else if (event.modifier.empty) {
                        updateInputState { doEnd() }
                    }

                    Key.ARROW_LEFT -> if (event.modifier.onlyShift) {
                        updateInputState { doShiftLeft() }
                    } else if (event.modifier.empty) {
                        updateInputState { doArrowLeft() }
                    }

                    Key.ARROW_RIGHT -> if (event.modifier.onlyShift) {
                        updateInputState { doShiftRight() }
                    } else if (event.modifier.empty) {
                        updateInputState { doArrowRight() }
                    }

                    Key.C -> if (event.modifier.onlyControl) {
                        val selectionText = textInputState.selectionText
                        clipboard.text = selectionText
                    }

                    Key.V -> if (event.modifier.onlyControl) {
                        updateInputState { commitText(clipboard.text) }
                    }

                    Key.X -> if (event.modifier.onlyControl) {
                        val selectionText = textInputState.selectionText
                        clipboard.text = selectionText
                        updateInputState { removeSelection() }
                    }

                    else -> {}
                }
            }
            .then(modifier),
    ) { node ->
        with(canvas) {
            fillRect(offset = IntOffset.ZERO, size = node.size, color = Colors.WHITE)
            fillRect(offset = IntOffset(1, 1), size = node.size - 2, color = Colors.BLACK)
            val textOffset = IntOffset(6, node.height / 2 - 4)
            val textWidth = node.width - 8
            if (value.isEmpty() && !focused) {
                if (placeholder != null) {
                    drawText(
                        offset = textOffset,
                        width = textWidth,
                        text = placeholder,
                        color = Colors.LIGHT_GRAY
                    )
                }
            } else {
                // TODO handle composition region
                var textCursor = 0
                val beforeSelectionText = textInputState.text.substring(0, textInputState.selection.start)
                drawText(
                    offset = textOffset,
                    text = beforeSelectionText,
                    color = Colors.WHITE
                )
                textCursor += canvas.textMeasurer.measure(beforeSelectionText).width

                val selectionText = textInputState.selectionText
                val selectionWidth = canvas.textMeasurer.measure(selectionText).width
                val selectionOffset = textOffset + IntOffset(textCursor, 0)
                fillRect(
                    offset = selectionOffset,
                    size = IntSize(selectionWidth, 9),
                    color = Colors.GRAY,
                )
                drawText(
                    offset = selectionOffset,
                    text = selectionText,
                    color = Colors.WHITE
                )

                if (cursorShow && textInputState.selectionLeft) {
                    fillRect(
                        offset = textOffset + IntOffset(textCursor, 0),
                        size = IntSize(1, 9),
                        color = Colors.WHITE,
                    )
                }

                textCursor += selectionWidth

                if (cursorShow && !textInputState.selectionLeft) {
                    fillRect(
                        offset = textOffset + IntOffset(textCursor, 0),
                        size = IntSize(1, 9),
                        color = Colors.WHITE,
                    )
                }

                val afterSelectionText = textInputState.text.substring(textInputState.selection.end)
                drawText(
                    offset = textOffset + IntOffset(textCursor, 0),
                    text = afterSelectionText,
                    color = Colors.WHITE
                )
            }
        }
    }
}