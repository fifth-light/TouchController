package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vector4f;
import net.minecraft.world.RaycastContext;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.fifthlight.touchcontroller.helper.PerspectiveInvertible;
import top.fifthlight.touchcontroller.layout.CrosshairStatus;
import top.fifthlight.touchcontroller.model.ControllerHudModel;

@Mixin(GameRenderer.class)
public abstract class CrosshairTargetMixin {
    @Shadow
    @Final
    private Camera camera;

    @Shadow
    public abstract Matrix4f getBasicProjectionMatrix(Camera camera, float tickDelta, boolean changingFov);

    @Unique
    private Vec3d getCrosshairDirection(float tickDelta, double cameraPitchRadians, double cameraYawRadians) {
        ControllerHudModel controllerHudModel = KoinJavaComponent.get(ControllerHudModel.class);
        CrosshairStatus crosshairStatus = controllerHudModel.getResult().getCrosshairStatus();

        Vector4f ndc;
        if (crosshairStatus == null) {
            ndc = new Vector4f(0, 0, -1f, 1f);
        } else {
            Vec2f screen = new Vec2f(crosshairStatus.getPositionX(), crosshairStatus.getPositionY());
            ndc = new Vector4f(2 * screen.x - 1, 1 - 2 * screen.y, -1f, 1f);
        }

        Matrix4f projectionMatrix = getBasicProjectionMatrix(camera, tickDelta, true);
        Matrix4f invertedProjectionMatrix = ((PerspectiveInvertible) (Object) projectionMatrix).touchController$invertPerspective();
        ndc.transform(invertedProjectionMatrix);
        Vector4f pointerDirection = ndc;
        Vec3d direction = new Vec3d(-pointerDirection.getX(), pointerDirection.getY(), 1f).normalize();

        return direction.rotateX((float) -cameraPitchRadians).rotateY((float) -cameraYawRadians);
    }

    @Unique
    private static Vec3d currentDirection;

    @Redirect(
            method = "updateTargetedEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;",
                    ordinal = 0
            )
    )
    private HitResult cameraRaycast(Entity instance, double maxDistance, float tickDelta, boolean includeFluids) {
        double cameraPitch = Math.toRadians(instance.getPitch(tickDelta));
        double cameraYaw = Math.toRadians(instance.getYaw(tickDelta));

        Vec3d position = instance.getCameraPosVec(tickDelta);
        Vec3d direction = getCrosshairDirection(tickDelta, cameraPitch, cameraYaw);
        currentDirection = direction;
        Vec3d interactionTarget = position.add(direction.x * maxDistance, direction.y * maxDistance, direction.z * maxDistance);
        RaycastContext.FluidHandling fluidHandling = includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE;
        return instance.world.raycast(new RaycastContext(position, interactionTarget, RaycastContext.ShapeType.OUTLINE, fluidHandling, instance));
    }

    @Redirect(
            method = "updateTargetedEntity",
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
