package top.fifthlight.touchcontroller.ext

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.serializer
import top.fifthlight.touchcontroller.config.LayoutLayer
import top.fifthlight.touchcontroller.config.LayoutLayerCondition
import top.fifthlight.touchcontroller.control.ControllerWidget

class LayoutLayerSerializer : KSerializer<LayoutLayer> {
    private val widgetSerializer = serializer<ControllerWidget>()
    private val widgetListSerializer = ListSerializer(widgetSerializer)

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("top.fifthlight.touchcontroller.config.TouchControllerLayout") {
            element<String>("name")
            element<List<ControllerWidget>>("widgets")
            element<LayoutLayerCondition>("condition")
        }

    override fun serialize(encoder: Encoder, value: LayoutLayer) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
            encodeSerializableElement(descriptor, 1, widgetListSerializer, value.widgets)
            encodeSerializableElement(descriptor, 2, serializer(), value.condition)
        }
    }

    override fun deserialize(decoder: Decoder): LayoutLayer {
        var name: String
        var widgets: PersistentList<ControllerWidget>
        var condition: LayoutLayerCondition
        return decoder.decodeStructure(descriptor) {
            name = decodeStringElement(descriptor, 0)
            widgets = decodeSerializableElement(descriptor, 1, widgetListSerializer).toPersistentList()
            condition = decodeSerializableElement(descriptor, 2, serializer())
            LayoutLayer(
                name = name,
                widgets = widgets,
                condition = condition
            )
        }
    }
}