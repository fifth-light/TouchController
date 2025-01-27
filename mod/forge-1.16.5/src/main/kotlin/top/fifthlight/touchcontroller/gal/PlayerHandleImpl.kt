package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft
import net.minecraft.entity.item.BoatEntity
import net.minecraft.entity.item.minecart.MinecartEntity
import net.minecraft.entity.passive.PigEntity
import net.minecraft.entity.passive.StriderEntity
import net.minecraft.entity.passive.horse.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import top.fifthlight.combine.data.ItemStack
import top.fifthlight.combine.platform.ItemStackImpl
import top.fifthlight.combine.platform.toCombine
import top.fifthlight.touchcontroller.config.ItemList
import top.fifthlight.touchcontroller.mixin.ClientPlayerInteractionManagerInvoker

@JvmInline
value class PlayerHandleImpl(val inner: PlayerEntity) : PlayerHandle {
    private val client: Minecraft
        get() = Minecraft.getInstance()

    override fun hasItemsOnHand(list: ItemList): Boolean = Hand.entries.any { hand ->
        inner.getItemInHand(hand).toCombine().item in list
    }

    override fun changeLookDirection(deltaYaw: Double, deltaPitch: Double) {
        // Magic value 0.15 from net.minecraft.entity.Entity#turn
        inner.turn(deltaYaw / 0.15, deltaPitch / 0.15)
    }

    override var currentSelectedSlot: Int
        get() = inner.inventory.selected
        set(value) {
            inner.inventory.selected = value
        }

    override fun dropSlot(index: Int) {
        if (index == currentSelectedSlot) {
            inner.drop(true)
            return
        }

        val originalSlot = currentSelectedSlot
        val interactionManagerAccessor = client.gameMode as ClientPlayerInteractionManagerInvoker

        // Can it trigger anti-cheat?
        currentSelectedSlot = index
        interactionManagerAccessor.callSyncSelectedSlot()

        inner.drop(true)

        currentSelectedSlot = originalSlot
        interactionManagerAccessor.callSyncSelectedSlot()
    }

    override fun getInventorySlot(index: Int): ItemStack = ItemStackImpl(inner.inventory.getItem(index))

    override val isUsingItem: Boolean
        get() = inner.isUsingItem

    override val onGround: Boolean
        get() = inner.isOnGround

    override var isFlying: Boolean
        get() = inner.abilities.flying
        set(value) {
            inner.abilities.flying = value
        }

    override val isSubmergedInWater: Boolean
        get() = inner.isUnderWater

    override val isTouchingWater: Boolean
        get() = inner.isInWater

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
            is MinecartEntity -> RidingEntityType.MINECART
            is BoatEntity -> RidingEntityType.BOAT
            is PigEntity -> RidingEntityType.PIG
            is HorseEntity, is DonkeyEntity, is MuleEntity, is ZombieHorseEntity, is SkeletonHorseEntity -> RidingEntityType.HORSE
            is LlamaEntity -> RidingEntityType.LLAMA
            is StriderEntity -> RidingEntityType.STRIDER
            else -> RidingEntityType.OTHER
        }

    override val canFly: Boolean
        get() = inner.abilities.mayfly
}

object PlayerHandleFactoryImpl : PlayerHandleFactory {
    private val client = Minecraft.getInstance()

    override fun getPlayerHandle(): PlayerHandle? = client.player?.let(::PlayerHandleImpl)
}