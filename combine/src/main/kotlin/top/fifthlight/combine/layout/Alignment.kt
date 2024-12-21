package top.fifthlight.combine.layout

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize

@Stable
fun interface Alignment {
    fun align(size: IntSize, space: IntSize): IntOffset

    @Stable
    fun interface Horizontal {
        fun align(size: Int, space: Int): Int

        operator fun plus(other: Vertical): Alignment = CombinedAlignment(this, other)
    }

    @Stable
    fun interface Vertical {
        fun align(size: Int, space: Int): Int

        operator fun plus(other: Horizontal): Alignment = CombinedAlignment(other, this)
    }

    companion object {
        @Stable
        val TopLeft: Alignment = BiasAlignment(-1f, -1f)
        @Stable
        val TopCenter: Alignment = BiasAlignment(0f, -1f)
        @Stable
        val TopRight: Alignment = BiasAlignment(1f, -1f)
        @Stable
        val CenterLeft: Alignment = BiasAlignment(-1f, 0f)
        @Stable
        val Center: Alignment = BiasAlignment(0f, 0f)
        @Stable
        val CenterRight: Alignment = BiasAlignment(1f, 0f)
        @Stable
        val BottomLeft: Alignment = BiasAlignment(-1f, 1f)
        @Stable
        val BottomCenter: Alignment = BiasAlignment(0f, 1f)
        @Stable
        val BottomRight: Alignment = BiasAlignment(1f, 1f)

        @Stable
        val Top: Vertical = BiasAlignment.Vertical(-1f)
        @Stable
        val CenterVertically: Vertical = BiasAlignment.Vertical(0f)
        @Stable
        val Bottom: Vertical = BiasAlignment.Vertical(1f)

        @Stable
        val Left: Horizontal = BiasAlignment.Horizontal(-1f)
        @Stable
        val CenterHorizontally: Horizontal = BiasAlignment.Horizontal(0f)
        @Stable
        val Right: Horizontal = BiasAlignment.Horizontal(1f)
    }
}

private class CombinedAlignment(
    private val horizontal: Alignment.Horizontal,
    private val vertical: Alignment.Vertical,
) : Alignment {
    override fun align(size: IntSize, space: IntSize): IntOffset {
        val x = horizontal.align(size.width, space.width)
        val y = vertical.align(size.height, space.height)
        return IntOffset(x, y)
    }
}

object AbsoluteAlignment {
    @Stable
    val TopLeft: Alignment = BiasAbsoluteAlignment(-1f, -1f)
    @Stable
    val TopRight: Alignment = BiasAbsoluteAlignment(1f, -1f)
    @Stable
    val CenterLeft: Alignment = BiasAbsoluteAlignment(-1f, 0f)
    @Stable
    val CenterRight: Alignment = BiasAbsoluteAlignment(1f, 0f)
    @Stable
    val BottomLeft: Alignment = BiasAbsoluteAlignment(-1f, 1f)
    @Stable
    val BottomRight: Alignment = BiasAbsoluteAlignment(1f, 1f)

    @Stable
    val Left: Alignment.Horizontal = BiasAbsoluteAlignment.Horizontal(-1f)
    @Stable
    val Right: Alignment.Horizontal = BiasAbsoluteAlignment.Horizontal(1f)
}

@Immutable
data class BiasAlignment(val horizontalBias: Float, val verticalBias: Float) : Alignment {
    override fun align(size: IntSize, space: IntSize): IntOffset {
        val centerX = (space.width - size.width).toFloat() / 2f
        val centerY = (space.height - size.height).toFloat() / 2f

        val x = centerX * (1 + horizontalBias)
        val y = centerY * (1 + verticalBias)
        return IntOffset(x.toInt(), y.toInt())
    }

    @Immutable
    data class Horizontal(val bias: Float) : Alignment.Horizontal {
        override fun align(size: Int, space: Int): Int {
            val center = (space - size).toFloat() / 2f
            return (center * (1 + bias)).toInt()
        }

        override fun plus(other: Alignment.Vertical): Alignment {
            return when (other) {
                is Vertical -> BiasAlignment(bias, other.bias)
                else -> super.plus(other)
            }
        }
    }

    @Immutable
    data class Vertical(val bias: Float) : Alignment.Vertical {
        override fun align(size: Int, space: Int): Int {
            val center = (space - size).toFloat() / 2f
            return (center * (1 + bias)).toInt()
        }

        override fun plus(other: Alignment.Horizontal): Alignment {
            return when (other) {
                is Horizontal -> BiasAlignment(other.bias, bias)
                is BiasAbsoluteAlignment.Horizontal -> BiasAbsoluteAlignment(other.bias, bias)
                else -> super.plus(other)
            }
        }
    }
}

@Immutable
data class BiasAbsoluteAlignment(val horizontalBias: Float, val verticalBias: Float) : Alignment {
    override fun align(size: IntSize, space: IntSize): IntOffset {
        val remaining = IntSize(space.width - size.width, space.height - size.height)
        val centerX = remaining.width.toFloat() / 2f
        val centerY = remaining.height.toFloat() / 2f

        val x = centerX * (1 + horizontalBias)
        val y = centerY * (1 + verticalBias)
        return IntOffset(x.toInt(), y.toInt())
    }

    @Immutable
    data class Horizontal(val bias: Float) : Alignment.Horizontal {
        override fun align(size: Int, space: Int): Int {
            val center = (space - size).toFloat() / 2f
            return (center * (1 + bias)).toInt()
        }

        override fun plus(other: Alignment.Vertical): Alignment {
            return when (other) {
                is BiasAlignment.Vertical -> BiasAbsoluteAlignment(bias, other.bias)
                else -> super.plus(other)
            }
        }
    }
}

