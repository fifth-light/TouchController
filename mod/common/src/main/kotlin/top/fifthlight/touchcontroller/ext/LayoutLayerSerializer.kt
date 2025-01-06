package top.fifthlight.touchcontroller.ext

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlinx.serialization.serializer
import top.fifthlight.touchcontroller.config.DEFAULT_LAYER_NAME
import top.fifthlight.touchcontroller.config.LayoutLayer
import top.fifthlight.touchcontroller.config.LayoutLayerCondition
import top.fifthlight.touchcontroller.control.ControllerWidget

class LayoutLayerSerializer : KSerializer<LayoutLayer> {
    private val widgetSerializer = serializer<ControllerWidget>()
    private val widgetListSerializer = ListSerializer(widgetSerializer)

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("top.fifthlight.touchcontroller.config.LayoutLayer") {
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
        var name: String? = null
        var widgets: PersistentList<ControllerWidget> = persistentListOf()
        var condition = LayoutLayerCondition()
        return decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> name = decodeStringElement(descriptor, 0)
                    1 -> widgets = decodeSerializableElement(descriptor, 1, widgetListSerializer).toPersistentList()
                    2 -> condition = decodeSerializableElement(descriptor, 2, serializer())
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            LayoutLayer(
                name = name ?: DEFAULT_LAYER_NAME,
                widgets = widgets,
                condition = condition
            )
        }
    }
}