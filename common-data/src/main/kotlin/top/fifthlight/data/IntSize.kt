package top.fifthlight.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

fun IntSize(size: Int) = IntSize(packInts(size, size))
fun IntSize(width: Int, height: Int) = IntSize(packInts(width, height))

@Serializable(with = IntSizeSerializer::class)
@JvmInline
value class IntSize internal constructor(private val packed: Long) {
    val width
        get() = unpackInt1(packed)

    val height
        get() = unpackInt2(packed)

    companion object {
        val ZERO = IntSize(0)
    }

    operator fun contains(offset: Offset): Boolean {
        val x = 0 <= offset.x && offset.x < width
        val y = 0 <= offset.y && offset.y < height
        return x && y
    }

    operator fun contains(offset: IntOffset): Boolean {
        val x = offset.x in 0..<width
        val y = offset.y in 0..<height
        return x && y
    }

    operator fun component1() = width
    operator fun component2() = height
    operator fun plus(length: Int) = IntSize(width = width + length, height = height + length)
    operator fun minus(size: Int) = IntSize(width = width - size, height = height - size)
    operator fun minus(size: IntSize) = IntOffset(x = width - size.width, y = height - size.height)
    operator fun minus(offset: IntOffset) = IntSize(width = width - offset.x, height = height - offset.y)
    operator fun times(num: Int) = IntSize(width = width * num, height = height * num)
    operator fun div(num: Int) = IntSize(width = width / num, height = height / num)
    fun toSize() = Size(width = width.toFloat(), height = height.toFloat())

    override fun toString(): String {
        return "IntSize(width=$width, height=$height)"
    }
}

private class IntSizeSerializer : KSerializer<IntSize> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("top.fifthlight.data.IntSize") {
        element<Int>("width")
        element<Int>("height")
    }

    override fun serialize(encoder: Encoder, value: IntSize) = encoder.encodeStructure(descriptor) {
        encodeIntElement(descriptor, 0, value.width)
        encodeIntElement(descriptor, 1, value.height)
    }

    override fun deserialize(decoder: Decoder): IntSize = decoder.decodeStructure(descriptor) {
        val x = decodeIntElement(descriptor, 0)
        val y = decodeIntElement(descriptor, 1)
        IntSize(x, y)
    }
}
