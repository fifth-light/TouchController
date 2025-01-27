package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft
import net.minecraft.util.math.RayTraceResult
import top.fifthlight.touchcontroller.mixin.ClientPlayerInteractionManagerInvoker

object ViewActionProviderImpl : ViewActionProvider {
    private val client = Minecraft.getInstance()

    override fun getCrosshairTarget(): CrosshairTarget? {
        val target = client.hitResult ?: return null
        return when (target.type) {
            RayTraceResult.Type.ENTITY -> CrosshairTarget.ENTITY
            RayTraceResult.Type.BLOCK -> CrosshairTarget.BLOCK
            RayTraceResult.Type.MISS -> CrosshairTarget.MISS
            else -> return null
        }
    }

    override fun getCurrentBreakingProgress(): Float {
        val manager = client.gameMode
        val accessor = manager as ClientPlayerInteractionManagerInvoker
        return accessor.currentBreakingProgress
    }
}