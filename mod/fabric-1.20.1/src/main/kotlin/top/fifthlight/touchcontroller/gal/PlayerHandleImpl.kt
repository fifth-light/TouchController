package top.fifthlight.touchcontroller.gal

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.mob.SkeletonHorseEntity
import net.minecraft.entity.mob.ZombieHorseEntity
import net.minecraft.entity.passive.*
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.entity.vehicle.MinecartEntity
import net.minecraft.util.Hand
import top.fifthlight.combine.data.ItemStack
import top.fifthlight.combine.platform.ItemStackImpl
import top.fifthlight.combine.platform.toCombine
import top.fifthlight.touchcontroller.config.ItemList
import top.fifthlight.touchcontroller.mixin.ClientPlayerInteractionManagerInvoker

@JvmInline
value class PlayerHandleImpl(val inner: ClientPlayerEntity) : PlayerHandle {
    private val client: MinecraftClient
        get() = MinecraftClient.getInstance()

    override fun hasItemsOnHand(list: ItemList): Boolean = Hand.entries.any { hand ->
        inner.getStackInHand(hand).toCombine().item in list
    }

    override fun changeLookDirection(deltaYaw: Double, deltaPitch: Double) {
        // Magic value 0.15 from net.minecraft.entity.Entity.changeLookDirection
        inner.changeLookDirection(deltaYaw / 0.15, deltaPitch / 0.15)
    }

    override var currentSelectedSlot: Int
        get() = inner.inventory.selectedSlot
        set(value) {
            inner.inventory.selectedSlot = value
        }

    override fun dropSlot(index: Int) {
        if (index == currentSelectedSlot) {
            inner.dropSelectedItem(true)
            return
        }

        val originalSlot = currentSelectedSlot
        val interactionManagerAccessor = client.interactionManager as ClientPlayerInteractionManagerInvoker

        // Can it trigger anti-cheat?
        currentSelectedSlot = index
        interactionManagerAccessor.callSyncSelectedSlot()

        inner.dropSelectedItem(true)

        currentSelectedSlot = originalSlot
        interactionManagerAccessor.callSyncSelectedSlot()
    }

    override fun getInventorySlot(index: Int): ItemStack = ItemStackImpl(inner.inventory.getStack(index))

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
        get() = inner.isSubmergedInWater

    override val isTouchingWater: Boolean
        get() = inner.isTouchingWater

    override var isSprinting: Boolean
        get() = inner.isSprinting
        set(value) {
            inner.isSprinting = value
        }

    override var isSneaking: Boolean
        get() = inner.isSneaking
        set(value) {
            inner.isSneaking = value
        }

    override val ridingEntityType: RidingEntityType?
        get() = when (inner.vehicle) {
            null -> null
            is MinecartEntity -> RidingEntityType.MINECART
            is BoatEntity -> RidingEntityType.BOAT
            is PigEntity -> RidingEntityType.PIG
            is CamelEntity -> RidingEntityType.CAMEL
            is HorseEntity, is DonkeyEntity, is MuleEntity, is ZombieHorseEntity, is SkeletonHorseEntity -> RidingEntityType.HORSE
            is LlamaEntity -> RidingEntityType.LLAMA
            is StriderEntity -> RidingEntityType.STRIDER
            else -> RidingEntityType.OTHER
        }

    override val canFly: Boolean
        get() = inner.abilities.allowFlying
}

object PlayerHandleFactoryImpl : PlayerHandleFactory {
    private val client = MinecraftClient.getInstance()

    override fun getPlayerHandle(): PlayerHandle? = client.player?.let(::PlayerHandleImpl)
}