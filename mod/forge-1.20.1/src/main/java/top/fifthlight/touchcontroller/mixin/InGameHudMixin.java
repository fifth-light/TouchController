package top.fifthlight.touchcontroller.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.event.RenderEvents;
import top.fifthlight.touchcontroller.model.ControllerHudModel;

@Mixin(Gui.class)
public abstract class InGameHudMixin {
    @Shadow
    @Final
    private static ResourceLocation GUI_ICONS_LOCATION;
    @Shadow
    protected int screenWidth;
    @Shadow
    protected int screenHeight;
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = "renderCrosshair",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void renderCrosshair(GuiGraphics context, CallbackInfo callbackInfo) {
        boolean shouldRender = RenderEvents.INSTANCE.shouldRenderCrosshair();
        if (!shouldRender) {
            if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
                float attackCooldownProgress = this.minecraft.player.getAttackStrengthScale(0.0f);
                boolean renderFullTexture = false;
                if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && attackCooldownProgress >= 1.0f) {
                    renderFullTexture = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0f && this.minecraft.crosshairPickEntity.isAlive();
                }
                int x = screenWidth / 2;
                int y = screenHeight / 2;
                if (renderFullTexture) {
                    context.blit(GUI_ICONS_LOCATION, x - 8, y - 8, 68, 94, 16, 16);
                } else if (attackCooldownProgress < 1.0f) {
                    int progress = (int) (attackCooldownProgress * 17.0f);
                    context.blit(GUI_ICONS_LOCATION, x - 8, y - 2, 36, 94, 16, 4);
                    context.blit(GUI_ICONS_LOCATION, x - 8, y - 2, 52, 94, progress, 4);
                }
            }
            RenderSystem.defaultBlendFunc();
            callbackInfo.cancel();
        }
    }

    @Inject(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",
                    ordinal = 0
            )
    )
    private void renderHotbar(float tickDelta, GuiGraphics context, CallbackInfo ci) {
        var player = minecraft.player;
        if (player != null) {
            var controllerHudModel = (ControllerHudModel) KoinJavaComponent.get(ControllerHudModel.class);
            var inventory = controllerHudModel.getResult().getInventory();
            var slots = inventory.getSlots();
            var x = (screenWidth - 182) / 2 + 1;
            var y = screenHeight - 22 + 1;
            for (int i = 0; i < 9; i++) {
                var stack = player.getInventory().getItem(i);
                if (stack.isEmpty()) {
                    continue;
                }
                var slot = slots[i];
                var progress = slot.getProgress();
                var height = (int) (16 * progress);
                context.fill(x + 20 * i + 2, y + 18 - height, x + 20 * i + 18, y + 18, 0xFF00BB00);
            }
        }
    }
}