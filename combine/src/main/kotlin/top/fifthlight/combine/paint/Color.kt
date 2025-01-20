package top.fifthlight.combine.paint

@JvmInline
value class Color(val value: Int) {
    val a: Int
        get() = (value shr 24) and 0xFF
    val r: Int
        get() = (value shr 16) and 0xFF
    val g: Int
        get() = (value shr 8) and 0xFF
    val b: Int
        get() = value and 0xFF
}

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
    val ALTERNATE_WHITE = Color(0xFFBABABAu)
    val BLACK = Color(0xFF000000u)
    val GRAY = Color(0xFF808080u)
    val LIGHT_GRAY = Color(0xFFA0A0A0u)
    val RED = Color(0xFFFF0000u)
    val GREEN = Color(0xFF00FF00u)
    val BLUE = Color(0xFF0000FFu)
}