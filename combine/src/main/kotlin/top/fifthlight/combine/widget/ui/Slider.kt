package top.fifthlight.combine.widget.ui

import androidx.compose.runtime.*
import top.fifthlight.combine.input.pointer.PointerEventType
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.combine.modifier.placement.height
import top.fifthlight.combine.modifier.pointer.onPointerInput
import top.fifthlight.combine.paint.Colors
import top.fifthlight.combine.sound.LocalSoundManager
import top.fifthlight.combine.sound.SoundKind
import top.fifthlight.combine.widget.base.Canvas
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize

@Composable
fun IntSlider(
    modifier: Modifier,
    range: IntRange,
    value: Int,
    onValueChanged: (Int) -> Unit,
) {
    fun Int.toProgress() = (this - range.first).toFloat() / (range.last - range.first)
    fun Float.toValue() = (this * (range.last - range.first)).toInt() + range.first

    Slider(
        modifier = modifier,
        range = 0f..1f,
        value = value.toProgress(),
        onValueChanged = {
            onValueChanged(it.toValue())
        },
    )
}

@Composable
fun Slider(
    modifier: Modifier,
    range: ClosedFloatingPointRange<Float>,
    value: Float,
    onValueChanged: (Float) -> Unit,
) {
    val soundManager = LocalSoundManager.current

    fun Float.toValue() = this * (range.endInclusive - range.start) + range.start
    fun Float.toProgress() = (this - range.start) / (range.endInclusive - range.start)

    val progress = value.toProgress()

    var pressed by remember { mutableStateOf(false) }

    Canvas(
        modifier = Modifier
            .height(height = 16)
            .onPointerInput { event ->
                when (event.type) {
                    PointerEventType.Press -> {
                        pressed = true
                        val relativePos = event.position - absolutePosition
                        val rawProgress = relativePos.x / size.width
                        val newProgress = rawProgress.coerceIn(0f, 1f)
                        onValueChanged(newProgress.toValue())
                    }

                    PointerEventType.Release -> {
                        pressed = false
                        soundManager.play(SoundKind.BUTTON_PRESS, 1f)
                    }

                    PointerEventType.Cancel -> {
                        pressed = false
                    }

                    PointerEventType.Move -> {
                        if (pressed) {
                            val relativePos = event.position - absolutePosition
                            val rawProgress = relativePos.x / size.width
                            val newProgress = rawProgress.coerceIn(0f, 1f)
                            onValueChanged(newProgress.toValue())
                        }
                    }

                    else -> return@onPointerInput false
                }
                true
            }
            .then(modifier),
    ) { node ->
        with(canvas) {
            fillRect(IntOffset.ZERO, IntSize(node.width, 16), Colors.WHITE)
            fillRect(IntOffset.ZERO, IntSize((node.width * progress).toInt(), 16), Colors.BLACK)
        }
    }
}