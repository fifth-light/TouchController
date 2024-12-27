package top.fifthlight.touchcontroller.layout

interface InventoryActionProvider {
    fun hasPlayer(): Boolean
    fun currentSelectedSlot(): Int
}