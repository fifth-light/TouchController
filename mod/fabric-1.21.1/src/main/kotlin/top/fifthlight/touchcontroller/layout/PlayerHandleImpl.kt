package top.fifthlight.touchcontroller.layout

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Hand
import top.fifthlight.combine.platform.toCombine
import top.fifthlight.touchcontroller.config.TouchControllerConfig

@JvmInline
value class PlayerHandleImpl(val inner: PlayerEntity) : PlayerHandle {
    override fun haveUsableItemsOnHand(config: TouchControllerConfig): Boolean = Hand.entries.any { hand ->
        inner.getStackInHand(hand).item.toCombine() in config.usableItems
    }
}

object PlayerHandleFactoryImpl : PlayerHandleFactory {
    private val client = MinecraftClient.getInstance()

    override fun getPlayerHandle(): PlayerHandle? = client.player?.let(::PlayerHandleImpl)
}