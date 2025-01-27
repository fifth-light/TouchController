package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.fifthlight.touchcontroller.model.ControllerHudModel;

@Mixin(GameRenderer.class)
public abstract class CrosshairTargetMixin {
    @Shadow
    @Final
    private Camera mainCamera;

    @Shadow
    public abstract Matrix4f getProjectionMatrix(double pFov);

    @Shadow
    protected abstract double getFov(Camera pActiveRenderInfo, float pPartialTicks, boolean pUseFOVSetting);

    @Unique
    private Vec3 touchController$getCrosshairDirection(double fov, double cameraPitchRadians, double cameraYawRadians) {
        var controllerHudModel = (ControllerHudModel) KoinJavaComponent.get(ControllerHudModel.class);
        var crosshairStatus = controllerHudModel.getResult().getCrosshairStatus();

        Vector4d ndc;
        if (crosshairStatus == null) {
            ndc = new Vector4d(0, 0, -1f, 1f);
        } else {
            var screen = new Vector2d(crosshairStatus.getPositionX(), crosshairStatus.getPositionY());
            ndc = new Vector4d(2 * screen.x - 1, 1 - 2 * screen.y, -1f, 1f);
        }

        var inverseProjectionMatrix = getProjectionMatrix(fov).invert();
        var pointerDirection = ndc.mul(inverseProjectionMatrix);
        var direction = new Vector3d(-pointerDirection.x, pointerDirection.y, 1f).normalize();

        var normalizedDirection = direction.rotateX(cameraPitchRadians).rotateY(-cameraYawRadians);

        return new Vec3(normalizedDirection.x, normalizedDirection.y, normalizedDirection.z);
    }

    @Unique
    private static Vec3 currentDirection;

    @Redirect(
            method = "pick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;",
                    ordinal = 0
            )
    )
    private HitResult cameraRaycast(Entity instance, double pHitDistance, float pPartialTicks, boolean pHitFluids) {
        var fov = getFov(mainCamera, pPartialTicks, true);
        var cameraPitch = Math.toRadians(instance.getViewXRot(pPartialTicks));
        var cameraYaw = Math.toRadians(instance.getViewYRot(pPartialTicks));

        var position = instance.getEyePosition(pPartialTicks);
        var direction = touchController$getCrosshairDirection(fov, cameraPitch, cameraYaw);
        currentDirection = direction;
        var interactionTarget = position.add(direction.x * pHitDistance, direction.y * pHitDistance, direction.z * pHitDistance);
        var clipContextFluid = pHitFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE;
        return instance.level().clip(new ClipContext(position, interactionTarget, ClipContext.Block.OUTLINE, clipContextFluid, instance));
    }

    @Redirect(
            method = "pick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getViewVector(F)Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 0
            )
    )
    private Vec3 getRotationVec(Entity instance, float tickDelta) {
        return currentDirection;
    }
}
