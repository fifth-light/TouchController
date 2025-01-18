package top.fifthlight.touchcontroller.ext

import net.minecraft.client.renderer.BufferBuilder
import top.fifthlight.combine.paint.Color

fun BufferBuilder.color(color: Color): BufferBuilder =
    color(color.r, color.g, color.b, color.a)
