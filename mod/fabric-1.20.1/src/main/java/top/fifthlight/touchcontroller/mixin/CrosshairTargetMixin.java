package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.*;
import top.fifthlight.touchcontroller.model.ControllerHudModel;

@Mixin(GameRenderer.class)
public abstract class CrosshairTargetMixin {
    @Shadow
    @Final
    MinecraftClient client;

    @Shadow
    @Final
    private Camera camera;

    @Shadow
    public abstract Matrix4f getBasicProjectionMatrix(double par1);

    @Shadow
    protected abstract double getFov(Camera par1, float par2, boolean par3);

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
    public HitResult entityRaycast(Entity entity, Vec3d rotation, double maxDistance, float tickDelta, boolean includeFluids) {
        Vec3d position = entity.getCameraPosVec(tickDelta);
        Vec3d maxReachablePosition = position.add(rotation.x * maxDistance, rotation.y * maxDistance, rotation.z * maxDistance);
        return entity.getWorld().raycast(new RaycastContext(position, maxReachablePosition, RaycastContext.ShapeType.OUTLINE, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, entity));
    }

    /**
     * @author fifth_light
     * @reason Overwrite the findCrosshairTarget to change the crosshair target to touch crosshair
     */
    @Overwrite
    public void updateTargetedEntity(float tickDelta) {

        Entity cameraEntity = this.client.getCameraEntity();
        if (cameraEntity == null) {
            return;
        }
        if (this.client.world == null) {
            return;
        }
        this.client.getProfiler().push("pick");
        this.client.targetedEntity = null;
        double reachDistance = this.client.interactionManager.getReachDistance();

        // CHANGES BEGIN
        // Original: this.client.crosshairTarget = cameraEntity.raycast(reachDistance, tickDelta, false);
        var fov = getFov(camera, tickDelta, true);
        var cameraPitch = Math.toRadians(cameraEntity.getPitch(tickDelta));
        var cameraYaw = Math.toRadians(cameraEntity.getYaw(tickDelta));
        var cameraRotationVec = getCrosshairDirection(fov, cameraPitch, cameraYaw);
        this.client.crosshairTarget = entityRaycast(cameraEntity, cameraRotationVec, reachDistance, tickDelta, false);
        // CHANGES END

        Vec3d cameraPosition = cameraEntity.getCameraPosVec(tickDelta);
        boolean reachDistanceGreaterThanEntityReachDistance = false;
        if (this.client.interactionManager.hasExtendedReach()) {
            reachDistance = 6.0;
        } else {
            if (reachDistance > 3.0) {
                reachDistanceGreaterThanEntityReachDistance = true;
            }
        }
        double squaredTargetDistance = reachDistance * reachDistance;
        if (this.client.crosshairTarget != null) {
            squaredTargetDistance = this.client.crosshairTarget.getPos().squaredDistanceTo(cameraPosition);
        }

        // Removed: Vec3d cameraRotationVec = cameraEntity.getRotationVec(1.0f);
        Vec3d cameraMaxReachablePosition = cameraPosition.add(cameraRotationVec.x * reachDistance, cameraRotationVec.y * reachDistance, cameraRotationVec.z * reachDistance);
        Box cametaBoundingBox = cameraEntity.getBoundingBox().stretch(cameraRotationVec.multiply(reachDistance)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(cameraEntity, cameraPosition, cameraMaxReachablePosition, cametaBoundingBox, entity -> !entity.isSpectator() && entity.canHit(), reachDistance);
        if (entityHitResult != null) {
            Entity hitResultEntity = entityHitResult.getEntity();
            Vec3d hitResultPos = entityHitResult.getPos();
            double cameraToHitResultSquaredDistance = cameraPosition.squaredDistanceTo(hitResultPos);
            if (reachDistanceGreaterThanEntityReachDistance && cameraToHitResultSquaredDistance > 9.0) {
                this.client.crosshairTarget = BlockHitResult.createMissed(hitResultPos, Direction.getFacing(cameraRotationVec.x, cameraRotationVec.y, cameraRotationVec.z), BlockPos.ofFloored(hitResultPos));
            } else if (cameraToHitResultSquaredDistance < squaredTargetDistance || this.client.crosshairTarget == null) {
                this.client.crosshairTarget = entityHitResult;
                if (hitResultEntity instanceof LivingEntity || hitResultEntity instanceof ItemFrameEntity) {
                    this.client.targetedEntity = hitResultEntity;
                }
            }
        }
        this.client.getProfiler().pop();
    }
}
