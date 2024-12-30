package top.fifthlight.combine.data

import androidx.compose.runtime.Immutable
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize

@Immutable
data class Texture(
    val identifier: Identifier,
    val size: IntSize,
    val atlasOffset: IntOffset,
)