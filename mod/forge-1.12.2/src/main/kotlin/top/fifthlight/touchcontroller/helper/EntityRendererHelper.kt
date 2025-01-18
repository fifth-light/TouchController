@file:Suppress("unused")

package top.fifthlight.touchcontroller.helper

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.EntityRenderer
import net.minecraft.entity.Entity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.config.GlobalConfigHolder
import top.fifthlight.touchcontroller.model.ControllerHudModel

object EntityRendererHelper : KoinComponent {
    private val globalConfigHolder: GlobalConfigHolder by inject()
    private val controllerHudModel: ControllerHudModel by inject()
    private val client = Minecraft.getMinecraft()

    @JvmStatic
    fun doDisableMouseDirection(): Boolean {
        var config = globalConfigHolder.config.value
        return config.disableMouseMove
    }

    private fun getProjectionMatrix(farPlaneDistance: Float, fov: Float): Matrix4f {
        val aspect = client.displayWidth.toFloat() / client.displayHeight.toFloat()
        return Matrix4f().setPerspective(Math.toRadians(fov.toDouble()).toFloat(), aspect, 0.05f, farPlaneDistance * MathHelper.SQRT_2)
    }

    private fun getViewVector(fov: Float, farPlaneDistance: Float, entity: Entity, partialTicks: Float): Vec3d {
        val crosshairStatus = controllerHudModel.result.crosshairStatus

        val ndc = if (crosshairStatus == null) {
            Vector4f(0f, 0f, -1f, 1f)
        } else {
            val screen = Vector2f(crosshairStatus.positionX, crosshairStatus.positionY)
            Vector4f(2 * screen.x - 1, 1 - 2 * screen.y, -1f, 1f)
        }

        val inverseProjectionMatrix = getProjectionMatrix(farPlaneDistance, fov).invert()
        val pointerDirection = ndc.mul(inverseProjectionMatrix)
        val direction = Vector3f(-pointerDirection.x, pointerDirection.y, 1f).normalize()

        val cameraPitchDegrees = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks
        val cameraPitchRadians = Math.toRadians(cameraPitchDegrees.toDouble()).toFloat()
        val cameraYawDegrees = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks
        val cameraYawRadians = Math.toRadians(cameraYawDegrees.toDouble()).toFloat()
        val normalizedDirection = direction.rotateX(cameraPitchRadians).rotateY(-cameraYawRadians)

        return Vec3d(normalizedDirection.x.toDouble(), normalizedDirection.y.toDouble(), normalizedDirection.z.toDouble())
    }

    @JvmStatic
    fun getLook(entity: Entity, entityRenderer: EntityRenderer, partialTicks: Float): Vec3d {
        val fov = entityRenderer.getFOVModifier(partialTicks, true)
        return getViewVector(fov, entityRenderer.farPlaneDistance, entity, partialTicks)
    }

    @JvmStatic
    fun rayTrace(entity: Entity, blockReachDistance: Double, partialTicks: Float, entityRenderer: EntityRenderer): RayTraceResult? {
        val position = entity.getPositionEyes(partialTicks)
        val fov = entityRenderer.getFOVModifier(partialTicks, true)
        val direction = getViewVector(fov, entityRenderer.farPlaneDistance, entity, partialTicks);
        val endPosition = position.add(direction.x * blockReachDistance, direction.y * blockReachDistance, direction.z * blockReachDistance);
        return entity.world.rayTraceBlocks(position, endPosition, false, false, true)
    }

    @JvmStatic
    fun doDisableBlockOutline() = controllerHudModel.result.crosshairStatus == null
}
