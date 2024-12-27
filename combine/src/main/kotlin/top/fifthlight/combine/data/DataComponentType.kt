package top.fifthlight.combine.data

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.collections.immutable.PersistentList


val LocalDataComponentTypeFactory =
    staticCompositionLocalOf<DataComponentTypeFactory> { error("No DataComponentTypeFactory in context") }

interface DataComponentTypeFactory {
    fun of(id: Identifier): DataComponentType?

    val allComponents: PersistentList<DataComponentType>
}

interface DataComponentType {
    val id: Identifier?

    fun listItems(): PersistentList<Item>
}