package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.animal.Pig
import net.minecraft.world.entity.animal.camel.Camel
import net.minecraft.world.entity.animal.horse.*
import net.minecraft.world.entity.monster.Strider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.entity.vehicle.Minecart
import top.fifthlight.combine.data.ItemStack
import top.fifthlight.combine.platform.ItemStackImpl
import top.fifthlight.combine.platform.toCombine
import top.fifthlight.touchcontroller.config.ItemList
import top.fifthlight.touchcontroller.mixin.MultiPlayerGameModeInvoker

@JvmInline
value class PlayerHandleImpl(val inner: Player) : PlayerHandle {
    private val client: Minecraft
        get() = Minecraft.getInstance()

    override fun hasItemsOnHand(list: ItemList): Boolean = InteractionHand.entries.any { hand ->
        inner.getItemInHand(hand).item.toCombine() in list
    }

    override fun changeLookDirection(deltaYaw: Double, deltaPitch: Double) {
        // Magic value 0.15 from net.minecraft.world.entity.Entity#turn
        inner.turn(deltaYaw / 0.15, deltaPitch / 0.15)
    }

    override var currentSelectedSlot: Int
        get() = inner.inventory.selected
        set(value) {
            inner.inventory.selected = value
        }

    override fun dropSlot(index: Int) {
        if (index == currentSelectedSlot) {
            inner.inventory.removeFromSelected(true)
            return
        }

        val originalSlot = currentSelectedSlot
        val interactionManagerAccessor = client.gameMode as MultiPlayerGameModeInvoker

        // Can it trigger anti-cheat?
        currentSelectedSlot = index
        interactionManagerAccessor.callEnsureHasSentCarriedItem()

        inner.inventory.removeFromSelected(true)

        currentSelectedSlot = originalSlot
        interactionManagerAccessor.callEnsureHasSentCarriedItem()
    }

    override fun getInventorySlot(index: Int): ItemStack = ItemStackImpl(inner.inventory.getItem(index))

    override val isUsingItem: Boolean
        get() = inner.isUsingItem

    override val onGround: Boolean
        get() = inner.onGround()

    override var isFlying: Boolean
        get() = inner.abilities.flying
        set(value) {
            inner.abilities.flying = value
        }

    override val isSubmergedInWater: Boolean
        get() = inner.isUnderWater

    override var isSprinting: Boolean
        get() = inner.isSprinting
        set(value) {
            inner.isSprinting = value
        }

    override val isSneaking: Boolean
        get() = inner.isSteppingCarefully

    override val ridingEntityType: RidingEntityType?
        get() = when (inner.vehicle) {
            null -> null
            is Minecart -> RidingEntityType.MINECART
            is Boat -> RidingEntityType.BOAT
            is Pig -> RidingEntityType.PIG
            is Camel -> RidingEntityType.CAMEL
            is Horse, is Donkey, is Mule, is ZombieHorse, is SkeletonHorse -> RidingEntityType.HORSE
            is Llama -> RidingEntityType.LLAMA
            is Strider -> RidingEntityType.STRIDER
            else -> RidingEntityType.OTHER
        }
}

object PlayerHandleFactoryImpl : PlayerHandleFactory {
    private val client = Minecraft.getInstance()

    override fun getPlayerHandle(): PlayerHandle? = client.player?.let(::PlayerHandleImpl)
}