package top.fifthlight.touchcontroller.gal

import net.minecraft.client.MinecraftClient
import net.minecraft.util.hit.HitResult
import top.fifthlight.touchcontroller.mixin.ClientPlayerInteractionManagerMixin

object ViewActionProviderImpl : ViewActionProvider {
    private val client = MinecraftClient.getInstance()

    override fun getCrosshairTarget(): CrosshairTarget? {
        val target = client.crosshairTarget ?: return null
        return when (target.type) {
            HitResult.Type.ENTITY -> CrosshairTarget.ENTITY
            HitResult.Type.BLOCK -> CrosshairTarget.BLOCK
            HitResult.Type.MISS -> CrosshairTarget.MISS
            else -> return null
        }
    }

    override fun getCurrentBreakingProgress(): Float {
        val manager = client.interactionManager
        val accessor = manager as ClientPlayerInteractionManagerMixin
        return accessor.currentBreakingProgress
    }
}