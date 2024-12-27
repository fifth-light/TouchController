package top.fifthlight.combine.paint

import top.fifthlight.combine.data.Text
import top.fifthlight.data.IntSize

interface TextMeasurer {
    fun measure(text: String): IntSize
    fun measure(text: String, maxWidth: Int): IntSize
    fun measure(text: Text): IntSize
    fun measure(text: Text, maxWidth: Int): IntSize
}