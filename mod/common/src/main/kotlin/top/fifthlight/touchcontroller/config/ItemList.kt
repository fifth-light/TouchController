package top.fifthlight.touchcontroller.config

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
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
    @SerialName("components")
    private val _components: ComponentTypesList = ComponentTypesList(),
    val projectile: Boolean = false,
    val rangedWeapon: Boolean = false,
) {
    constructor(
        whitelist: PersistentList<Item> = persistentListOf(),
        blacklist: PersistentList<Item> = persistentListOf(),
        projectile: Boolean = false,
        rangedWeapon: Boolean = false,
        components: PersistentList<DataComponentType> = persistentListOf(),
    ) : this(
        _whitelist = ItemsList(whitelist),
        _blacklist = ItemsList(blacklist),
        projectile = projectile,
        rangedWeapon = rangedWeapon,
        _components = ComponentTypesList(components)
    )

    val whitelist: PersistentList<Item>
        get() = _whitelist.items
    val blacklist: PersistentList<Item>
        get() = _blacklist.items
    val components: PersistentList<DataComponentType>
        get() = _components.items

    fun copy(
        whitelist: PersistentList<Item> = this.whitelist,
        blacklist: PersistentList<Item> = this.blacklist,
        projectile: Boolean = this.projectile,
        rangedWeapon: Boolean = this.rangedWeapon,
        components: PersistentList<DataComponentType> = this.components,
    ) = ItemList(
        _whitelist = ItemsList(whitelist),
        _blacklist = ItemsList(blacklist),
        projectile = projectile,
        rangedWeapon = rangedWeapon,
        _components = ComponentTypesList(components),
    )

    operator fun contains(item: Item): Boolean {
        return when {
            item in blacklist -> false
            item in whitelist -> true
            projectile && item.isProjectile -> true
            rangedWeapon && item.isRangedWeapon -> true
            components.any { item.containComponents(it) } -> true
            else -> false
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemList

        if (projectile != other.projectile) return false
        if (rangedWeapon != other.rangedWeapon) return false
        if (_whitelist != other._whitelist) return false
        if (_blacklist != other._blacklist) return false
        if (_components != other._components) return false

        return true
    }

    override fun hashCode(): Int {
        var result = projectile.hashCode()
        result = 31 * result + rangedWeapon.hashCode()
        result = 31 * result + _whitelist.hashCode()
        result = 31 * result + _blacklist.hashCode()
        result = 31 * result + _components.hashCode()
        return result
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
