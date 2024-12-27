package top.fifthlight.touchcontroller.ext

import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import top.fifthlight.touchcontroller.config.LayoutLayer
import top.fifthlight.touchcontroller.config.TouchControllerLayout
import top.fifthlight.touchcontroller.control.ControllerWidget

@OptIn(ExperimentalSerializationApi::class)
class TouchControllerLayoutSerializer : KSerializer<TouchControllerLayout> {
    private class PersistentListDescriptor : SerialDescriptor by serialDescriptor<List<ControllerWidget>>() {
        override val serialName: String = "top.fifthlight.touchcontroller.config.TouchControllerLayout"
    }

    private val itemSerializer = serializer<LayoutLayer>()

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: TouchControllerLayout) {
        return ListSerializer(itemSerializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): TouchControllerLayout {
        return ListSerializer(itemSerializer).deserialize(decoder).toPersistentList()
    }
}