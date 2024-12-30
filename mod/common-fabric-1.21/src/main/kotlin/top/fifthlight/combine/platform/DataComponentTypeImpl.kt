package top.fifthlight.combine.platform

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import net.minecraft.component.ComponentType
import net.minecraft.registry.Registries
import top.fifthlight.combine.data.DataComponentType
import top.fifthlight.combine.data.DataComponentTypeFactory
import top.fifthlight.combine.data.Identifier
import top.fifthlight.combine.data.Item
import kotlin.jvm.optionals.getOrNull

object DataComponentTypeFactoryImpl : DataComponentTypeFactory {
    override val supportDataComponents: Boolean = true

    override fun of(id: Identifier): DataComponentType? =
        Registries.DATA_COMPONENT_TYPE.get(id.toMinecraft())?.let { DataComponentTypeImpl(it) }

    override val allComponents: PersistentList<DataComponentType> by lazy {
        Registries.DATA_COMPONENT_TYPE.map { DataComponentTypeImpl(it) }.toPersistentList()
    }
}

data class DataComponentTypeImpl(
    val inner: ComponentType<*>,
) : DataComponentType {
    override val id: Identifier?
        get() = Registries.DATA_COMPONENT_TYPE.getKey(inner).getOrNull()?.value?.toCombine()

    override val allItems: PersistentList<Item> by lazy {
        Registries.ITEM
            .filter { this.inner in it.components }
            .map { it.toCombine() }
            .toPersistentList()
    }
}
