package top.fifthlight.combine.paint

import top.fifthlight.data.IntSize

interface TextMeasurer {
    fun measure(text: String): IntSize
    fun measure(text: String, maxWidth: Int): IntSize
}