package top.fifthlight.touchcontroller.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.event.RenderEvents;
import top.fifthlight.touchcontroller.layout.InventoryResult;
import top.fifthlight.touchcontroller.layout.InventorySlotStatus;
import top.fifthlight.touchcontroller.model.ControllerHudModel;

@Mixin(IngameGui.class)
public abstract class InGameHudMixin {
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Shadow
    protected int screenWidth;

    @Shadow
    protected int screenHeight;

    @Inject(
            method = "renderCrosshair",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/IngameGui;blit(Lcom/mojang/blaze3d/matrix/MatrixStack;IIIIII)V",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void renderCrosshair(MatrixStack matrices, CallbackInfo callbackInfo) {
        IngameGui self = (IngameGui) (Object) this;
        boolean shouldRender = RenderEvents.INSTANCE.shouldRenderCrosshair();
        if (!shouldRender) {
            if (this.minecraft.options.attackIndicator == AttackIndicatorStatus.CROSSHAIR) {
                float attackCooldownProgress = this.minecraft.player.getAttackStrengthScale(0.0f);
                boolean renderFullTexture = false;
                if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && attackCooldownProgress >= 1.0f) {
                    renderFullTexture = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0f && this.minecraft.crosshairPickEntity.isAlive();
                }
                int x = screenWidth / 2;
                int y = screenHeight / 2;
                if (renderFullTexture) {
                    self.blit(matrices, x - 8, y - 8, 68, 94, 16, 16);
                } else if (attackCooldownProgress < 1.0f) {
                    int progress = (int) (attackCooldownProgress * 17.0f);
                    self.blit(matrices, x - 8, y - 2, 36, 94, 16, 4);
                    self.blit(matrices, x - 8, y - 2, 52, 94, progress, 4);
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
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;defaultBlendFunc()V",
                    ordinal = 0
            )
    )
    private void renderHotbar(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        ClientPlayerEntity player = minecraft.player;
        if (player != null) {
            ControllerHudModel controllerHudModel = KoinJavaComponent.get(ControllerHudModel.class);
            InventoryResult inventory = controllerHudModel.getResult().getInventory();
            InventorySlotStatus[] slots = inventory.getSlots();
            int x = (screenWidth - 182) / 2 + 1;
            int y = screenHeight - 22 + 1;
            for (int i = 0; i < 9; i++) {
                ItemStack stack = player.inventory.getItem(i);
                if (stack.isEmpty()) {
                    continue;
                }
                InventorySlotStatus slot = slots[i];
                float progress = slot.getProgress();
                int height = (int) (16 * progress);
                IngameGui.fill(matrices, x + 20 * i + 2, y + 18 - height, x + 20 * i + 18, y + 18, 0xFF00BB00);
            }
        }
    }
}