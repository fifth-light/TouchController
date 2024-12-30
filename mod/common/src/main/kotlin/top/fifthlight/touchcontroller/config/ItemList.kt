package top.fifthlight.touchcontroller.config

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.data.*

@Immutable
@Serializable
class ItemList private constructor(
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
            item in blacklist -> false
            item in whitelist -> true
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

private class ItemsListSerializer : KSerializer<ItemsList>, KoinComponent {
    private val itemFactory: ItemFactory by inject()

    private class PersistentListDescriptor : SerialDescriptor by serialDescriptor<PersistentList<Item>>()

    private val itemSerializer = serializer<String>()

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: ItemsList) {
        val ids = value.items.map { it.id.toString() }
        ListSerializer(itemSerializer).serialize(encoder, ids)
    }

    override fun deserialize(decoder: Decoder): ItemsList {
        return ItemsList(ListSerializer(itemSerializer).deserialize(decoder).mapNotNull {
            itemFactory.createItem(Identifier(it))
        }.toPersistentList())
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
