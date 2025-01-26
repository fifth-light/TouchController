package top.fifthlight.combine.platform

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import top.fifthlight.combine.data.DataComponentType
import top.fifthlight.combine.data.DataComponentTypeFactory
import top.fifthlight.combine.data.Identifier

object FoodComponentTypeFactoryImpl : DataComponentTypeFactory {
    override val supportDataComponents: Boolean = true

    override fun of(id: Identifier) = if (id == FoodComponentImpl.id) {
        FoodComponentImpl
    } else {
        null
    }

    override val allComponents: PersistentList<DataComponentType> = persistentListOf(FoodComponentImpl)
}