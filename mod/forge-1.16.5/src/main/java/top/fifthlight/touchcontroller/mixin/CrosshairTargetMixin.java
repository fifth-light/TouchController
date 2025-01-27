package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
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
    @Unique
    private static Vector3d currentDirection;
    @Shadow
    @Final
    private ActiveRenderInfo mainCamera;

    @Shadow
    public abstract Matrix4f getProjectionMatrix(ActiveRenderInfo camera, float tickDelta, boolean changingFov);

    @Unique
    private Vector3d touchController$getCrosshairDirection(float tickDelta, double cameraPitchRadians, double cameraYawRadians) {
        ControllerHudModel controllerHudModel = KoinJavaComponent.get(ControllerHudModel.class);
        CrosshairStatus crosshairStatus = controllerHudModel.getResult().getCrosshairStatus();

        Vector4f ndc;
        if (crosshairStatus == null) {
            ndc = new Vector4f(0, 0, -1f, 1f);
        } else {
            Vector2f screen = new Vector2f(crosshairStatus.getPositionX(), crosshairStatus.getPositionY());
            ndc = new Vector4f(2 * screen.x - 1, 1 - 2 * screen.y, -1f, 1f);
        }

        Matrix4f projectionMatrix = getProjectionMatrix(mainCamera, tickDelta, true);
        Matrix4f invertedProjectionMatrix = ((PerspectiveInvertible) (Object) projectionMatrix).touchController$invertPerspective();
        ndc.transform(invertedProjectionMatrix);
        Vector4f pointerDirection = ndc;
        Vector3d direction = new Vector3d(-pointerDirection.x(), pointerDirection.y(), 1f).normalize();

        return direction.xRot((float) -cameraPitchRadians).yRot((float) -cameraYawRadians);
    }

    @Redirect(
            method = "pick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;pick(DFZ)Lnet/minecraft/util/math/RayTraceResult;",
                    ordinal = 0
            )
    )
    private RayTraceResult cameraRaycast(Entity instance, double maxDistance, float tickDelta, boolean includeFluids) {
        double cameraPitch = Math.toRadians(instance.getViewXRot(tickDelta));
        double cameraYaw = Math.toRadians(instance.getViewYRot(tickDelta));

        Vector3d position = instance.getEyePosition(tickDelta);
        Vector3d direction = touchController$getCrosshairDirection(tickDelta, cameraPitch, cameraYaw);
        currentDirection = direction;
        Vector3d interactionTarget = position.add(direction.x * maxDistance, direction.y * maxDistance, direction.z * maxDistance);
        RayTraceContext.FluidMode fluidHandling = includeFluids ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE;
        return instance.level.clip(new RayTraceContext(position, interactionTarget, RayTraceContext.BlockMode.OUTLINE, fluidHandling, instance));
    }

    @Redirect(
            method = "pick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;getViewVector(F)Lnet/minecraft/util/math/vector/Vector3d;",
                    ordinal = 0
            )
    )
    private Vector3d getRotationVec(Entity instance, float tickDelta) {
        return currentDirection;
    }
}
