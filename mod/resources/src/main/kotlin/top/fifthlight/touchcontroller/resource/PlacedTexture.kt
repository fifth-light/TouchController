package top.fifthlight.touchcontroller.resource

import kotlinx.serialization.Serializable
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize

@Serializable
data class PlacedTexture(
    val position: IntOffset,
    val size: IntSize,
)
