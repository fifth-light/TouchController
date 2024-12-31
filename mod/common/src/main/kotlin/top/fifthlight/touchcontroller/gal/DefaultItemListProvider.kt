package top.fifthlight.touchcontroller.gal

import top.fifthlight.touchcontroller.config.ItemList

interface DefaultItemListProvider {
    val usableItems: ItemList
    val showCrosshairItems: ItemList
}