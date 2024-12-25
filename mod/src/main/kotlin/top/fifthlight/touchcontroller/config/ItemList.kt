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
import net.minecraft.component.ComponentType
import net.minecraft.item.Item
import net.minecraft.item.ProjectileItem
import net.minecraft.item.RangedWeaponItem
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import kotlin.jvm.optionals.getOrNull

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
        components: PersistentList<ComponentType<*>> = persistentListOf(),
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
    val components: PersistentList<ComponentType<*>>
        get() = _components.items

    fun copy(
        whitelist: PersistentList<Item> = this.whitelist,
        blacklist: PersistentList<Item> = this.blacklist,
        projectile: Boolean = this.projectile,
        rangedWeapon: Boolean = this.rangedWeapon,
        components: PersistentList<ComponentType<*>> = this.components,
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
            projectile && item is ProjectileItem -> true
            rangedWeapon && item is RangedWeaponItem -> true
            item.components.types.any { it in components } -> true
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

private class ItemsListSerializer : KSerializer<ItemsList> {
    private class PersistentListDescriptor : SerialDescriptor by serialDescriptor<PersistentList<Item>>()

    private val itemSerializer = serializer<String>()

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: ItemsList) {
        val ids = value.items.mapNotNull {
            Registries.ITEM.getKey(it).getOrNull()?.value?.toString()
        }
        ListSerializer(itemSerializer).serialize(encoder, ids)
    }

    override fun deserialize(decoder: Decoder): ItemsList {
        return ItemsList(ListSerializer(itemSerializer).deserialize(decoder).mapNotNull {
            Registries.ITEM.getOptionalValue(Identifier.of(it)).getOrNull()
        }.toPersistentList())
    }
}

@JvmInline
@Serializable(with = ComponentTypeSerializer::class)
value class ComponentTypesList(val items: PersistentList<ComponentType<*>> = persistentListOf())

private class ComponentTypeSerializer : KSerializer<ComponentTypesList> {
    private class PersistentListDescriptor : SerialDescriptor by serialDescriptor<PersistentList<Item>>()

    private val itemSerializer = serializer<String>()

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: ComponentTypesList) {
        val ids = value.items.mapNotNull {
            Registries.DATA_COMPONENT_TYPE.getKey(it)?.getOrNull()?.value?.toString()
        }
        ListSerializer(itemSerializer).serialize(encoder, ids)
    }

    override fun deserialize(decoder: Decoder): ComponentTypesList {
        return ComponentTypesList(ListSerializer(itemSerializer).deserialize(decoder).mapNotNull {
            Registries.DATA_COMPONENT_TYPE.getOptionalValue(Identifier.of(it)).getOrNull()
        }.toPersistentList())
    }
}
