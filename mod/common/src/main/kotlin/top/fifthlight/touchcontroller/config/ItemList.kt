package top.fifthlight.touchcontroller.config

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.*
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.data.*

@Immutable
@Serializable
@ConsistentCopyVisibility
data class ItemList private constructor(
    @SerialName("whitelist")
    private val _whitelist: ItemsList = ItemsList(),
    @SerialName("blacklist")
    private val _blacklist: ItemsList = ItemsList(),
    @SerialName("subclasses")
    private val _subclasses: ItemSubclassSet = ItemSubclassSet(),
    @SerialName("components")
    private val _components: ComponentTypesList = ComponentTypesList(),
) {
    constructor(
        whitelist: PersistentList<Item> = persistentListOf(),
        blacklist: PersistentList<Item> = persistentListOf(),
        components: PersistentList<DataComponentType> = persistentListOf(),
        subclasses: PersistentSet<ItemSubclass> = persistentSetOf(),
    ) : this(
        _whitelist = ItemsList(whitelist),
        _blacklist = ItemsList(blacklist),
        _components = ComponentTypesList(components),
        _subclasses = ItemSubclassSet(subclasses),
    )

    val whitelist: PersistentList<Item>
        get() = _whitelist.items
    val blacklist: PersistentList<Item>
        get() = _blacklist.items
    val components: PersistentList<DataComponentType>
        get() = _components.items
    val subclasses: PersistentSet<ItemSubclass>
        get() = _subclasses.items

    fun copy(
        whitelist: PersistentList<Item> = this.whitelist,
        blacklist: PersistentList<Item> = this.blacklist,
        components: PersistentList<DataComponentType> = this.components,
        subclasses: PersistentSet<ItemSubclass> = this.subclasses,
    ) = ItemList(
        _whitelist = ItemsList(whitelist),
        _blacklist = ItemsList(blacklist),
        _components = ComponentTypesList(components),
        _subclasses = ItemSubclassSet(subclasses),
    )

    operator fun contains(item: Item): Boolean {
        return when {
            blacklist.any { it.matches(item) } -> false
            whitelist.any { it.matches(item) } -> true
            components.any { item.containComponents(it) } -> true
            subclasses.any { item.isSubclassOf(it) } -> true
            else -> false
        }
    }
}

// Workaround of Kotlin serialization
@JvmInline
@Serializable(with = ItemsListSerializer::class)
value class ItemsList(val items: PersistentList<Item> = persistentListOf())

private class ItemSerializer : KSerializer<Item>, KoinComponent {
    private val itemFactory: ItemFactory by inject()

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("top.fifthlight.combine.data.Item") {
        element<Identifier>("id")
        element<Int?>("metadata")
    }

    override fun serialize(encoder: Encoder, value: Item) = encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, serializer<Identifier>(), value.id)
        @OptIn(ExperimentalSerializationApi::class)
        if (value is MetadataItem) {
            encodeNullableSerializableElement(descriptor, 1, serializer<Int?>(), value.metadata)
        } else {
            encodeNullableSerializableElement(descriptor, 1, serializer<Int?>(), null)
        }
    }

    override fun deserialize(decoder: Decoder): Item {
        val factory = itemFactory
        return if (factory is MetadataItemFactory) {
            decoder.decodeStructure(descriptor) {
                var id: Identifier? = null
                var metadata: Int? = null
                while (true) {
                    @OptIn(ExperimentalSerializationApi::class)
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> id = decodeSerializableElement(descriptor, 0, serializer<Identifier>())
                        1 -> metadata = decodeNullableSerializableElement(descriptor, 1, serializer<Int?>(), metadata)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                require(id != null) { "No id provided" }
                factory.createItem(id, metadata) ?: error("Bad item identifier: $id")
            }
        } else {
            decoder.decodeStructure(descriptor) {
                var id: Identifier? = null
                while (true) {
                    @OptIn(ExperimentalSerializationApi::class)
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> id = decodeSerializableElement(descriptor, 0, serializer<Identifier>())
                        1 -> decodeNullableSerializableElement(descriptor, 1, serializer<Int?>(), null)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                require(id != null) { "No id provided" }
                factory.createItem(id) ?: error("Bad item identifier: $id")
            }
        }
    }
}

private class ItemsListSerializer : KSerializer<ItemsList> {
    private class PersistentListDescriptor : SerialDescriptor by serialDescriptor<PersistentList<Item>>()

    private val itemSerializer = ItemSerializer()

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: ItemsList) {
        ListSerializer(itemSerializer).serialize(encoder, value.items)
    }

    override fun deserialize(decoder: Decoder): ItemsList {
        return ItemsList(ListSerializer(itemSerializer).deserialize(decoder).toPersistentList())
    }
}

@JvmInline
@Serializable(with = ComponentTypeSerializer::class)
value class ComponentTypesList(val items: PersistentList<DataComponentType> = persistentListOf())

private class ComponentTypeSerializer : KSerializer<ComponentTypesList>, KoinComponent {
    private val dataComponentTypeFactory: DataComponentTypeFactory by inject()

    private class PersistentListDescriptor : SerialDescriptor by serialDescriptor<PersistentList<Item>>()

    private val itemSerializer = serializer<String>()

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: ComponentTypesList) {
        val ids = value.items.mapNotNull { it.id?.toString() }
        ListSerializer(itemSerializer).serialize(encoder, ids)
    }

    override fun deserialize(decoder: Decoder): ComponentTypesList {
        return ComponentTypesList(ListSerializer(itemSerializer).deserialize(decoder).mapNotNull {
            dataComponentTypeFactory.of(Identifier(it))
        }.toPersistentList())
    }
}

@JvmInline
@Serializable(with = ItemSubclassSetSerializer::class)
value class ItemSubclassSet(val items: PersistentSet<ItemSubclass> = persistentSetOf())

private class ItemSubclassSetSerializer : KSerializer<ItemSubclassSet>, KoinComponent {
    private val itemFactory: ItemFactory by inject()

    private class PersistentSetDescriptor : SerialDescriptor by serialDescriptor<PersistentSet<Item>>()

    private val itemSerializer = serializer<String>()

    override val descriptor: SerialDescriptor = PersistentSetDescriptor()

    override fun serialize(encoder: Encoder, value: ItemSubclassSet) {
        val ids = value.items.map { it.configId }.toSet()
        SetSerializer(itemSerializer).serialize(encoder, ids)
    }

    override fun deserialize(decoder: Decoder): ItemSubclassSet {
        return ItemSubclassSet(SetSerializer(itemSerializer).deserialize(decoder).mapNotNull { id ->
            itemFactory.subclasses.firstOrNull { it.configId == id }
        }.toPersistentSet())
    }
}
