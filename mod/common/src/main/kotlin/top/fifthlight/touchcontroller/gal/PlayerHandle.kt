package top.fifthlight.touchcontroller.gal

import top.fifthlight.combine.data.ItemStack
import top.fifthlight.touchcontroller.config.ItemList

enum class RidingEntityType {
    MINECART,
    BOAT,
    PIG,
    HORSE,
    LLAMA,
    CAMEL,
    STRIDER,
    OTHER,
}

interface PlayerHandle {
    fun hasItemsOnHand(list: ItemList): Boolean
    fun changeLookDirection(deltaYaw: Double, deltaPitch: Double)
    var currentSelectedSlot: Int
    fun dropSlot(index: Int)
    fun getInventorySlot(index: Int): ItemStack
    val isUsingItem: Boolean
    val onGround: Boolean
    var isFlying: Boolean
    val isSubmergedInWater: Boolean
    var isSprinting: Boolean
    val isSneaking: Boolean
    val ridingEntityType: RidingEntityType?
}

interface PlayerHandleFactory {
    fun getPlayerHandle(): PlayerHandle?
}