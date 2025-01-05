package top.fifthlight.combine.input.input

data class TextRange(
    val start: Int,
    val length: Int,
) {
    init {
        require(start >= 0) { "Start of TextRange should not be negative: $start" }
        require(length >= 0) { "Length of TextRange should not be negative: $length" }
    }

    constructor(start: Int) : this(start, 0)

    val end get() = start + length

    companion object {
        val EMPTY = TextRange(0, 0)
    }
}

fun String.substring(range: TextRange) = substring(range.start, range.end)
fun String.removeRange(range: TextRange) = removeRange(range.start, range.end)
