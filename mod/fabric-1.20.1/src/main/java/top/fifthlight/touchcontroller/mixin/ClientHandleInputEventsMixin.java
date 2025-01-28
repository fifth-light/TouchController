package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.fifthlight.touchcontroller.model.ControllerHudModel;

@Mixin(MinecraftClient.class)
public abstract class ClientHandleInputEventsMixin {
    @Shadow
    @Final
    public GameOptions options;

    @Redirect(
            method = "handleInputEvents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z"
            )
    )
    private boolean wasPressed(KeyBinding instance) {
        var controllerHudModel = (ControllerHudModel) KoinJavaComponent.get(ControllerHudModel.class);
        var status = controllerHudModel.getStatus();
        if (instance == options.attackKey) {
            return instance.wasPressed() || status.getAttack().wasPressed();
        } else if (instance == options.useKey) {
            return instance.wasPressed() || status.getItemUse().wasPressed();
        } else if (instance == options.inventoryKey) {
            return instance.wasPressed() || status.getOpenInventory().wasPressed();
        } else if (instance == options.swapHandsKey) {
            return instance.wasPressed() || status.getSwapHands().wasPressed();
        } else {
            return instance.wasPressed();
        }
    }

    @Redirect(
            method = "handleInputEvents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"
            )
    )
    private boolean isPressed(KeyBinding instance) {
        var controllerHudModel = (ControllerHudModel) KoinJavaComponent.get(ControllerHudModel.class);
        var result = controllerHudModel.getResult();
        var status = controllerHudModel.getStatus();
        if (instance == options.attackKey) {
            return instance.isPressed() || status.getAttack().isPressed();
        } else if (instance == options.useKey) {
            return instance.isPressed() || status.getItemUse().isPressed();
        } else if (instance == options.inventoryKey) {
            return instance.isPressed() || status.getOpenInventory().isPressed();
        } else if (instance == options.swapHandsKey) {
            return instance.isPressed() || status.getSwapHands().isPressed();
        } else if (instance == options.sprintKey) {
            return instance.isPressed() || result.getSprint() || status.getSprintLocked();
        } else {
            return instance.isPressed();
        }
    }
}
