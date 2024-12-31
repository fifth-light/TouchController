package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.fifthlight.touchcontroller.model.ControllerHudModel;

@Mixin(GameRenderer.class)
public abstract class CrosshairTargetMixin {
    @Invoker("ensureTargetInRange")
    private static HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
        throw new AssertionError();
    }

    @Shadow
    protected abstract double getFov(Camera camera, float tickDelta, boolean changingFov);

    @Shadow
    @Final
    private Camera camera;

    @Shadow
    public abstract Matrix4f getBasicProjectionMatrix(double fov);

    @Unique
    private static HitResult findTargetWithDirection(Entity camera, Vec3d direction, double blockInteractionRange, double entityInteractionRange, float tickDelta) {
        double interactionRange = Math.max(blockInteractionRange, entityInteractionRange);
        double squaredInteractionRange = MathHelper.square(interactionRange);
        Vec3d position = camera.getCameraPosVec(tickDelta);
        Vec3d interactionTarget = position.add(direction.x * interactionRange, direction.y * interactionRange, direction.z * interactionRange);
        HitResult hitResult = camera.getWorld().raycast(new RaycastContext(position, interactionTarget, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, camera));

        double squaredHitResultDistance = hitResult.getPos().squaredDistanceTo(position);
        double targetDistance = interactionRange;
        if (hitResult.getType() != HitResult.Type.MISS) {
            squaredInteractionRange = squaredHitResultDistance;
            targetDistance = Math.sqrt(squaredInteractionRange);
        }
        Vec3d vec3d3 = position.add(direction.x * targetDistance, direction.y * targetDistance, direction.z * targetDistance);
        Box box = camera.getBoundingBox().stretch(direction.multiply(targetDistance)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, position, vec3d3, box, entity -> !entity.isSpectator() && entity.canHit(), squaredInteractionRange);
        if (entityHitResult != null && entityHitResult.getPos().squaredDistanceTo(position) < squaredHitResultDistance) {
            return ensureTargetInRange(entityHitResult, position, entityInteractionRange);
        }
        return ensureTargetInRange(hitResult, position, blockInteractionRange);
    }

    @Unique
    private Vec3d getCrosshairDirection(double fov, double cameraPitchRadians, double cameraYawRadians) {
        var controllerHudModel = (ControllerHudModel) KoinJavaComponent.get(ControllerHudModel.class);
        var crosshairStatus = controllerHudModel.getResult().getCrosshairStatus();

        Vector4d ndc;
        if (crosshairStatus == null) {
            ndc = new Vector4d(0, 0, -1f, 1f);
        } else {
            var screen = new Vector2d(crosshairStatus.getPositionX(), crosshairStatus.getPositionY());
            ndc = new Vector4d(2 * screen.x - 1, 1 - 2 * screen.y, -1f, 1f);
        }

        var inverseProjectionMatrix = getBasicProjectionMatrix(fov).invert();
        var pointerDirection = ndc.mul(inverseProjectionMatrix);
        var direction = new Vector3d(-pointerDirection.x, pointerDirection.y, 1f).normalize();

        var normalizedDirection = direction.rotateX(cameraPitchRadians).rotateY(-cameraYawRadians);

        return new Vec3d(normalizedDirection.x, normalizedDirection.y, normalizedDirection.z);
    }

    /**
     * @author fifth_light
     * @reason Overwrite the findCrosshairTarget to change the crosshair target to touch crosshair
     */
    @Overwrite
    private HitResult findCrosshairTarget(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta) {
        var fov = getFov(this.camera, tickDelta, true);
        var cameraPitch = Math.toRadians(camera.getPitch(tickDelta));
        var cameraYaw = Math.toRadians(camera.getYaw(tickDelta));
        var direction = getCrosshairDirection(fov, cameraPitch, cameraYaw);

        return findTargetWithDirection(camera, direction, blockInteractionRange, entityInteractionRange, tickDelta);
    }
}
