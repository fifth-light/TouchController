package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
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
    private Minecraft minecraft;

    @Shadow
    @Final
    private Camera mainCamera;

    @Shadow
    public abstract Matrix4f getProjectionMatrix(double par1);

    @Shadow
    protected abstract double getFov(Camera par1, float par2, boolean par3);

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
    public HitResult touchController$entityRaycast(Entity entity, Vec3 rotation, double maxDistance, float tickDelta, boolean pHitFluids) {
        Vec3 position = entity.getEyePosition(tickDelta);
        Vec3 maxReachablePosition = position.add(rotation.x * maxDistance, rotation.y * maxDistance, rotation.z * maxDistance);
        return entity.level().clip(new ClipContext(position, maxReachablePosition, ClipContext.Block.OUTLINE, pHitFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, entity));
    }

    /**
     * @author fifth_light
     * @reason Overwrite the findCrosshairTarget to change the crosshair target to touch crosshair
     */
    @Overwrite
    public void pick(float pPartialTicks) {
        Entity entity = this.minecraft.getCameraEntity();
        if (entity == null) {
            return;
        }
        if (this.minecraft.level == null) {
            return;
        }
        this.minecraft.getProfiler().push("pick");
        this.minecraft.crosshairPickEntity = null;
        double d0 = (double) this.minecraft.gameMode.getPickRange();
        double entityReach = this.minecraft.player.getEntityReach(); // Note - MC-76493 - We must validate players cannot click-through objects.

        // CHANGES BEGIN
        // Original: this.minecraft.hitResult = entity.pick(Math.max(d0, entityReach), pPartialTicks, false); // Run pick() with the max of the two, so we can prevent click-through.
        var fov = getFov(mainCamera, pPartialTicks, true);
        var cameraPitch = Math.toRadians(entity.getViewXRot(pPartialTicks));
        var cameraYaw = Math.toRadians(entity.getViewYRot(pPartialTicks));
        var cameraRotationVec = touchController$getCrosshairDirection(fov, cameraPitch, cameraYaw);
        this.minecraft.hitResult = touchController$entityRaycast(entity, cameraRotationVec, Math.max(d0, entityReach), pPartialTicks, false);
        // CHANGES END

        Vec3 vec3 = entity.getEyePosition(pPartialTicks);
        boolean flag = false;
        int i = 3;
        double d1 = d0;
        if (false && this.minecraft.gameMode.hasFarPickRange()) {
            d1 = 6.0D;
            d0 = d1;
        } else {
            if (d0 > 3.0D) {
                flag = true;
            }

            d0 = d0;
        }
        d0 = d1 = Math.max(d0, entityReach); // Pick entities with the max of both for the same reason.

        d1 *= d1;

        // If we picked a block, we need to ignore entities past that block. Added != MISS check to not truncate on failed picks.
        // Also fixes MC-250858
        if (this.minecraft.hitResult != null && this.minecraft.hitResult.getType() != HitResult.Type.MISS) {
            d1 = this.minecraft.hitResult.getLocation().distanceToSqr(vec3);
            double blockReach = this.minecraft.player.getBlockReach();
            // Discard the result as a miss if it is outside the block reach.
            if (d1 > blockReach * blockReach) {
                Vec3 pos = this.minecraft.hitResult.getLocation();
                this.minecraft.hitResult = BlockHitResult.miss(pos, Direction.getNearest(vec3.x, vec3.y, vec3.z), BlockPos.containing(pos));
            }
        }

        // Removed: Vec3 vec31 = entity.getViewVector(1.0F);
        Vec3 vec32 = vec3.add(cameraRotationVec.x * d0, cameraRotationVec.y * d0, cameraRotationVec.z * d0);
        float f = 1.0F;
        AABB aabb = entity.getBoundingBox().expandTowards(cameraRotationVec.scale(d0)).inflate(1.0D, 1.0D, 1.0D);
        EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(entity, vec3, vec32, aabb, (p_234237_) -> {
            return !p_234237_.isSpectator() && p_234237_.isPickable();
        }, d1);
        if (entityhitresult != null) {
            Entity entity1 = entityhitresult.getEntity();
            Vec3 vec33 = entityhitresult.getLocation();
            double d2 = vec3.distanceToSqr(vec33);
            if (d2 > d1 || d2 > entityReach * entityReach) { // Discard if the result is behind a block, or past the entity reach max. The var "flag" no longer has a use.
                this.minecraft.hitResult = BlockHitResult.miss(vec33, Direction.getNearest(cameraRotationVec.x, cameraRotationVec.y, cameraRotationVec.z), BlockPos.containing(vec33));
            } else if (d2 < d1 || this.minecraft.hitResult == null) {
                this.minecraft.hitResult = entityhitresult;
                if (entity1 instanceof LivingEntity || entity1 instanceof ItemFrame) {
                    this.minecraft.crosshairPickEntity = entity1;
                }
            }
        }

        this.minecraft.getProfiler().pop();
    }
}
