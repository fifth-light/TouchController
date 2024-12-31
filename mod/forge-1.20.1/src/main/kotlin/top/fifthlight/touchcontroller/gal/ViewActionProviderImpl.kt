package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft
import net.minecraft.world.phys.HitResult
import top.fifthlight.touchcontroller.mixin.MultiPlayerGameModeInvoker

object ViewActionProviderImpl : ViewActionProvider {
    private val client = Minecraft.getInstance()

    override fun getCrosshairTarget(): CrosshairTarget? {
        val target = client.hitResult ?: return null
        return when (target.type) {
            HitResult.Type.ENTITY -> CrosshairTarget.ENTITY
            HitResult.Type.BLOCK -> CrosshairTarget.BLOCK
            HitResult.Type.MISS -> CrosshairTarget.MISS
            else -> return null
        }
    }

    override fun getCurrentBreakingProgress(): Float {
        val manager = client.gameMode
        val accessor = manager as MultiPlayerGameModeInvoker
        return accessor.destroyProgress
    }
}