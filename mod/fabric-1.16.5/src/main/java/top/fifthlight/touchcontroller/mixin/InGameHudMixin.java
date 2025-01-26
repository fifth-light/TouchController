package top.fifthlight.touchcontroller.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.util.math.MatrixStack;
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

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow private int scaledWidth;

    @Shadow private int scaledHeight;

    @Inject(
            method = "renderCrosshair",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void renderCrosshair(MatrixStack matrices, CallbackInfo callbackInfo) {
        InGameHud self = (InGameHud) (Object) this;
        boolean shouldRender = RenderEvents.INSTANCE.shouldRenderCrosshair();
        if (!shouldRender) {
            if (this.client.options.attackIndicator == AttackIndicator.CROSSHAIR) {
                float attackCooldownProgress = this.client.player.getAttackCooldownProgress(0.0f);
                boolean renderFullTexture = false;
                if (this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity && attackCooldownProgress >= 1.0f) {
                    renderFullTexture = this.client.player.getAttackCooldownProgressPerTick() > 5.0f && this.client.targetedEntity.isAlive();
                }
                int x = scaledWidth / 2;
                int y = scaledHeight / 2;
                if (renderFullTexture) {
                    self.drawTexture(matrices, x - 8, y - 8, 68, 94, 16, 16);
                } else if (attackCooldownProgress < 1.0f) {
                    int progress = (int) (attackCooldownProgress * 17.0f);
                    self.drawTexture(matrices, x - 8, y - 2, 36, 94, 16, 4);
                    self.drawTexture(matrices, x - 8, y - 2, 52, 94, progress, 4);
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
        ClientPlayerEntity player = client.player;
        if (player != null) {
            ControllerHudModel controllerHudModel = KoinJavaComponent.get(ControllerHudModel.class);
            InventoryResult inventory = controllerHudModel.getResult().getInventory();
            InventorySlotStatus[] slots = inventory.getSlots();
            int x = (scaledWidth - 182) / 2 + 1;
            int y = scaledHeight - 22 + 1;
            for (int i = 0; i < 9; i++) {
                ItemStack stack = player.inventory.getStack(i);
                if (stack.isEmpty()) {
                    continue;
                }
                InventorySlotStatus slot = slots[i];
                float progress = slot.getProgress();
                int height = (int) (16 * progress);
                InGameHud.fill(matrices, x + 20 * i + 2, y + 18 - height, x + 20 * i + 18, y + 18, 0xFF00BB00);
            }
        }
    }
}