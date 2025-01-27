package top.fifthlight.combine.platform

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import top.fifthlight.combine.data.Item
import top.fifthlight.combine.data.ItemSubclass
import top.fifthlight.combine.data.Text

class ItemSubclassImpl<Clazz>(
    override val name: Text,
    override val configId: String,
    val clazz: Class<Clazz>,
) : ItemSubclass {
    override val id: String = clazz.simpleName

    override val items: PersistentList<Item> by lazy {
        ItemFactoryImpl.allItems.filter { it.isSubclassOf(this) }.toPersistentList()
    }
}