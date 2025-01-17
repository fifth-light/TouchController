package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft
import net.minecraft.util.math.RayTraceResult

object ViewActionProviderImpl : ViewActionProvider {
    private val client = Minecraft.getMinecraft()

    override fun getCrosshairTarget(): CrosshairTarget? {
        val target = client.objectMouseOver ?: return null
        return when (target.typeOfHit) {
            RayTraceResult.Type.ENTITY -> CrosshairTarget.ENTITY
            RayTraceResult.Type.BLOCK -> CrosshairTarget.BLOCK
            RayTraceResult.Type.MISS -> CrosshairTarget.MISS
            else -> return null
        }
    }

    override fun getCurrentBreakingProgress(): Float = client.playerController.curBlockDamageMP
}