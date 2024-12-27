package top.fifthlight.touchcontroller.ext

import net.minecraft.client.util.Window
import top.fifthlight.data.IntSize

val Window.size
    get() = IntSize(
        width = width,
        height = height
    )

val Window.scaledSize
    get() = IntSize(
        width = scaledWidth,
        height = scaledHeight
    )