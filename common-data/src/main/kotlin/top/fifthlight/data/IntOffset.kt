@file:Suppress("NOTHING_TO_INLINE")

package top.fifthlight.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*

inline fun IntOffset(offset: Int) = IntOffset(packInts(offset, offset))
inline fun IntOffset(x: Int, y: Int) = IntOffset(packInts(x, y))

@Serializable(with = IntOffsetSerializer::class)
@JvmInline
value class IntOffset(private val packed: Long) {
    val x
        get() = unpackInt1(packed)

    val y
        get() = unpackInt2(packed)

    val left
        get() = x
    val top
        get() = y

    companion object {
        val ZERO = IntOffset(0, 0)
    }

    fun toOffset() = Offset(x = x.toFloat(), y = y.toFloat())

    operator fun component1() = x
    operator fun component2() = y
    operator fun plus(length: Int) = IntOffset(x = x + length, y = y + length)
    operator fun plus(other: IntSize) = IntOffset(x = x + other.width, y = y + other.height)
    operator fun plus(other: IntOffset) = IntOffset(x = x + other.x, y = y + other.y)
    operator fun minus(length: Int) = IntOffset(x = x - length, y = y - length)
    operator fun minus(other: IntSize) = IntOffset(x = x - other.width, y = y - other.height)
    operator fun minus(other: IntOffset) = IntOffset(x = x - other.x, y = y - other.y)
    operator fun times(num: Int) = IntOffset(x = x * num, y = y * num)
    operator fun div(num: Int) = IntOffset(x = x / num, y = y / num)
    operator fun unaryMinus() = IntOffset(x = -x, y = -y)

    override fun toString(): String {
        return "IntOffset(left=$x, top=$y)"
    }
}

private class IntOffsetSerializer : KSerializer<IntOffset> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("top.fifthlight.data.IntOffset") {
        element<Int>("x")
        element<Int>("y")
    }

    override fun serialize(encoder: Encoder, value: IntOffset) = encoder.encodeStructure(descriptor) {
        encodeIntElement(descriptor, 0, value.x)
        encodeIntElement(descriptor, 1, value.y)
    }

    override fun deserialize(decoder: Decoder): IntOffset = decoder.decodeStructure(descriptor) {
        var x: Int? = null
        var y: Int? = null
        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> x = decodeIntElement(descriptor, 0)
                1 -> y = decodeIntElement(descriptor, 1)
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }
        require(x != null) { "Missing x in IntOffset" }
        require(y != null) { "Missing y in IntOffset" }
        IntOffset(x, y)
    }
}
