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
import top.fifthlight.touchcontroller.config.LayerConditionKey
import top.fifthlight.touchcontroller.config.LayerConditionValue
import top.fifthlight.touchcontroller.config.LayoutLayerCondition

class LayerConditionSerializer : KSerializer<LayoutLayerCondition> {
    private class PersistentMapDescriptor :
        SerialDescriptor by serialDescriptor<Map<LayerConditionKey, LayerConditionValue>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "top.fifthlight.touchcontroller.config.LayerConditionSerializer"
    }

    private val keySerializer = serializer<LayerConditionKey>()
    private val valueSerializer = serializer<LayerConditionValue>()
    private val mapSerializer = MapSerializer(keySerializer, valueSerializer)

    override val descriptor: SerialDescriptor = PersistentMapDescriptor()

    override fun serialize(encoder: Encoder, value: LayoutLayerCondition) =
        mapSerializer.serialize(encoder, value.conditions)

    override fun deserialize(decoder: Decoder) =
        LayoutLayerCondition(mapSerializer.deserialize(decoder).toPersistentMap())
}