package top.fifthlight.touchcontroller.ext

import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import top.fifthlight.touchcontroller.config.LayoutLayerCondition
import top.fifthlight.touchcontroller.config.LayoutLayerConditionKey
import top.fifthlight.touchcontroller.config.LayoutLayerConditionValue

class LayoutLayerConditionSerializer : KSerializer<LayoutLayerCondition> {
    private class PersistentMapDescriptor :
        SerialDescriptor by serialDescriptor<Map<LayoutLayerConditionKey, LayoutLayerConditionValue>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "top.fifthlight.touchcontroller.config.LayoutLayerCondition"
    }

    private val keySerializer = serializer<LayoutLayerConditionKey>()
    private val valueSerializer = serializer<LayoutLayerConditionValue>()
    private val mapSerializer = MapSerializer(keySerializer, valueSerializer)

    override val descriptor: SerialDescriptor = PersistentMapDescriptor()

    override fun serialize(encoder: Encoder, value: LayoutLayerCondition) =
        mapSerializer.serialize(encoder, value.conditions)

    override fun deserialize(decoder: Decoder) =
        LayoutLayerCondition(mapSerializer.deserialize(decoder).toPersistentMap())
}