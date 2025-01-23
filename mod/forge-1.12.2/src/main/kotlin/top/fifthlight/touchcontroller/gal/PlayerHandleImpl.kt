package top.fifthlight.touchcontroller.gal

import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.entity.item.EntityBoat
import net.minecraft.entity.item.EntityMinecart
import net.minecraft.entity.passive.*
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHand
import top.fifthlight.combine.data.ItemStack
import top.fifthlight.combine.platform.ItemStackImpl
import top.fifthlight.combine.platform.toCombine
import top.fifthlight.touchcontroller.config.ItemList

@JvmInline
value class PlayerHandleImpl(val inner: EntityPlayer) : PlayerHandle {
    private val client: Minecraft
        get() = Minecraft.getMinecraft()

    override fun hasItemsOnHand(list: ItemList): Boolean = EnumHand.entries.any { hand ->
        inner.getHeldItem(hand).toCombine().item in list
    }

    override fun changeLookDirection(deltaYaw: Double, deltaPitch: Double) {
        // Magic value 0.15 from net.minecraft.entity.Entity#turn
        inner.turn((deltaYaw / 0.15).toFloat(), -(deltaPitch / 0.15).toFloat())
    }

    override var currentSelectedSlot: Int
        get() = inner.inventory.currentItem
        set(value) {
            inner.inventory.currentItem = value
        }

    override fun dropSlot(index: Int) {
        if (index == currentSelectedSlot) {
            inner.dropItem(true)
            return
        }

        val originalSlot = currentSelectedSlot
        val interactionManager = client.playerController

        // Can it trigger anti-cheat?
        currentSelectedSlot = index
        interactionManager.syncCurrentPlayItem()

        inner.dropItem(true)

        currentSelectedSlot = originalSlot
        interactionManager.syncCurrentPlayItem()
    }

    override fun getInventorySlot(index: Int): ItemStack = ItemStackImpl(inner.inventory.getStackInSlot(index))

    override val isUsingItem: Boolean
        get() = !inner.activeItemStack.isEmpty

    override val onGround: Boolean
        get() = inner.onGround

    override var isFlying: Boolean
        get() = inner.capabilities.isFlying
        set(value) {
            inner.capabilities.isFlying = value
        }

    override val isSubmergedInWater: Boolean
        get() = inner.isInsideOfMaterial(Material.WATER)

    override var isSprinting: Boolean
        get() = inner.isSprinting
        set(value) {
            inner.isSprinting = value
        }

    override val isSneaking: Boolean
        get() = inner.isSneaking

    override val ridingEntityType: RidingEntityType?
        get() = when (inner.ridingEntity) {
            null -> null
            is EntityMinecart -> RidingEntityType.MINECART
            is EntityBoat -> RidingEntityType.BOAT
            is EntityPig -> RidingEntityType.PIG
            is EntityHorse, is EntityDonkey, is EntityMule, is EntityZombieHorse, is EntitySkeletonHorse -> RidingEntityType.HORSE
            is EntityLlama -> RidingEntityType.LLAMA
            else -> RidingEntityType.OTHER
        }
}

object PlayerHandleFactoryImpl : PlayerHandleFactory {
    private val client = Minecraft.getMinecraft()

    // client.player is NULLABLE
    @Suppress("UNNECESSARY_SAFE_CALL")
    override fun getPlayerHandle(): PlayerHandle? = client.player?.let(::PlayerHandleImpl)
}