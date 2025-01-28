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
import top.fifthlight.touchcontroller.layout.ContextResult;
import top.fifthlight.touchcontroller.layout.ContextStatus;
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
        ControllerHudModel controllerHudModel = KoinJavaComponent.get(ControllerHudModel.class);
        ContextStatus status = controllerHudModel.getStatus();
        if (instance == options.keyAttack) {
            return instance.wasPressed() || status.getAttack().wasPressed();
        } else if (instance == options.keyUse) {
            return instance.wasPressed() || status.getItemUse().wasPressed();
        } else if (instance == options.keyInventory) {
            return instance.wasPressed() || status.getOpenInventory().wasPressed();
        } else if (instance == options.keySwapHands) {
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
        ControllerHudModel controllerHudModel = KoinJavaComponent.get(ControllerHudModel.class);
        ContextResult result = controllerHudModel.getResult();
        ContextStatus status = controllerHudModel.getStatus();
        if (instance == options.keyAttack) {
            return instance.isPressed() || status.getAttack().isPressed();
        } else if (instance == options.keyUse) {
            return instance.isPressed() || status.getItemUse().isPressed();
        } else if (instance == options.keyInventory) {
            return instance.isPressed() || status.getOpenInventory().isPressed();
        } else if (instance == options.keySwapHands) {
            return instance.isPressed() || status.getSwapHands().isPressed();
        } else if (instance == options.keySprint) {
            return instance.isPressed() || result.getSprint() || status.getSprintLocked();
        } else {
            return instance.isPressed();
        }
    }
}
