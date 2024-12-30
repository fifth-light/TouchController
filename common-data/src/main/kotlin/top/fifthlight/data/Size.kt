package top.fifthlight.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlin.math.sqrt

fun Size(size: Float) = Size(packFloats(size, size))
fun Size(width: Float, height: Float) = Size(packFloats(width, height))

@Serializable
@JvmInline
value class Size internal constructor(private val packed: Long) {
    val width
        get() = unpackFloat1(packed)

    val height
        get() = unpackFloat2(packed)

    companion object {
        val ZERO = Size(0f, 0f)
        val ONE = Size(1f, 1f)
    }

    operator fun contains(offset: Offset): Boolean {
        val x = 0 <= offset.x && offset.x < width
        val y = 0 <= offset.y && offset.y < height
        return x && y
    }

    fun toIntSize() = IntSize(width = width.toInt(), height = height.toInt())

    operator fun component1() = width
    operator fun component2() = height
    operator fun plus(length: Float) = Size(width = width + length, height = height + length)
    operator fun minus(offset: Offset) = Size(width = width - offset.x, height = height - offset.y)
    operator fun times(num: Float) = Size(width = width * num, height = height * num)
    operator fun div(num: Float) = Size(width = width / num, height = height / num)

    val squaredLength
        get() = width * width + height * height
    val length
        get() = sqrt(squaredLength)

    override fun toString(): String {
        return "Size(width=$width, height=$height)"
    }
}

private class SizeSerializer : KSerializer<Size> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("top.fifthlight.data.Size") {
        element<Int>("width")
        element<Int>("height")
    }

    override fun serialize(encoder: Encoder, value: Size) = encoder.encodeStructure(descriptor) {
        encodeFloatElement(descriptor, 0, value.width)
        encodeFloatElement(descriptor, 1, value.height)
    }

    override fun deserialize(decoder: Decoder): Size = decoder.decodeStructure(descriptor) {
        var width: Float? = null
        var height: Float? = null
        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> width = decodeFloatElement(descriptor, 0)
                1 -> height = decodeFloatElement(descriptor, 1)
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }
        require(width != null) { "Missing width in Size" }
        require(height != null) { "Missing height in Size" }
        Size(width, height)
    }
}
