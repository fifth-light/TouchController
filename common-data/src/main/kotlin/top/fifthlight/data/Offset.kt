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
import kotlin.math.sqrt

fun Offset(offset: Float) = Offset(packFloats(offset, offset))
fun Offset(x: Float, y: Float) = Offset(packFloats(x, y))

@Serializable(with = OffsetSerializer::class)
@JvmInline
value class Offset internal constructor(private val packed: Long) {
    val x
        get() = unpackFloat1(packed)

    val y
        get() = unpackFloat2(packed)

    val left
        get() = x
    val top
        get() = y

    companion object {
        val ZERO = Offset(0f, 0f)
    }

    fun toIntOffset() = IntOffset(x = x.toInt(), y = y.toInt())

    operator fun component1() = x
    operator fun component2() = y
    operator fun plus(length: Float) = Offset(x = x + length, y = y + length)
    operator fun plus(offset: Offset) = Offset(x = x + offset.x, y = y + offset.y)
    operator fun minus(length: Float) = Offset(x = x - length, y = y - length)
    operator fun div(num: Float) = Offset(x = x / num, y = y / num)
    operator fun times(num: Float): Offset = Offset(x = x * num, y = y * num)
    operator fun minus(offset: IntOffset) = Offset(x = x - offset.x, y = y - offset.y)
    operator fun minus(offset: Offset) = Offset(x = x - offset.x, y = y - offset.y)
    operator fun minus(size: Size) = Offset(x = x - size.width, y = y - size.height)
    operator fun times(size: IntSize) = Offset(x = x * size.width.toFloat(), y = y * size.height.toFloat())
    operator fun unaryMinus() = Offset(x = -x, y = -y)

    val squaredLength
        get() = x * x + y * y
    val length
        get() = sqrt(squaredLength)

    override fun toString(): String {
        return "Offset(left=$left, top=$top)"
    }
}

private class OffsetSerializer : KSerializer<Offset> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("top.fifthlight.data.Offset") {
        element<Float>("x")
        element<Float>("y")
    }

    override fun serialize(encoder: Encoder, value: Offset) = encoder.encodeStructure(descriptor) {
        encodeFloatElement(descriptor, 0, value.x)
        encodeFloatElement(descriptor, 1, value.y)
    }

    override fun deserialize(decoder: Decoder): Offset = decoder.decodeStructure(descriptor) {
        val x = decodeFloatElement(descriptor, 0)
        val y = decodeFloatElement(descriptor, 1)
        Offset(x, y)
    }
}
