package top.fifthlight.touchcontroller.state

import top.fifthlight.data.IntSize

data class GlobalState(
    val inGame: Boolean = false,
    val windowSize: IntSize = IntSize.ZERO,
    val scaleFactor: Float = 1f,
)