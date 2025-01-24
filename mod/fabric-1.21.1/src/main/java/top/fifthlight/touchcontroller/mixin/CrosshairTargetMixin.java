package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.fifthlight.touchcontroller.model.ControllerHudModel;

@Mixin(GameRenderer.class)
public abstract class CrosshairTargetMixin {
    @Shadow
    protected abstract double getFov(Camera camera, float tickDelta, boolean changingFov);

    @Shadow
    @Final
    private Camera camera;

    @Shadow
    public abstract Matrix4f getBasicProjectionMatrix(double fov);

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

    @Unique
    private static Vec3d currentDirection;

    @Redirect(
            method = "findCrosshairTarget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;",
                    ordinal = 0
            )
    )
    private HitResult cameraRaycast(Entity instance, double maxDistance, float tickDelta, boolean includeFluids) {
        var fov = getFov(camera, tickDelta, true);
        var cameraPitch = Math.toRadians(instance.getPitch(tickDelta));
        var cameraYaw = Math.toRadians(instance.getYaw(tickDelta));

        var position = instance.getCameraPosVec(tickDelta);
        var direction = getCrosshairDirection(fov, cameraPitch, cameraYaw);
        currentDirection = direction;
        var interactionTarget = position.add(direction.x * maxDistance, direction.y * maxDistance, direction.z * maxDistance);
        var fluidHandling = includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE;
        return instance.getWorld().raycast(new RaycastContext(position, interactionTarget, RaycastContext.ShapeType.OUTLINE, fluidHandling, instance));
    }

    @Redirect(
            method = "findCrosshairTarget",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getRotationVec(F)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private Vec3d getRotationVec(Entity instance, float tickDelta) {
        return currentDirection;
    }
}
