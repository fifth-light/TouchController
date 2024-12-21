package top.fifthlight.combine.paint

@JvmInline
value class Color(val value: Int)

fun Color(value: UInt) = Color(value.toInt())

fun Color(a: Int, r: Int, g: Int, b: Int) = Color(
    ((a and 0xFF) shl 24) or
            ((r and 0xFF) shl 16) or
            ((g and 0xFF) shl 8) or
            (b and 0xFF)
)

fun Color(r: Int, g: Int, b: Int) = Color(0xFF, r, g, b)

object Colors {
    val WHITE = Color(0xFFFFFFFFu)
    val BLACK = Color(0xFF000000u)
    val RED = Color(0xFFFF0000u)
    val GREEN = Color(0xFF00FF00u)
    val BLUE = Color(0xFF0000FFu)
}