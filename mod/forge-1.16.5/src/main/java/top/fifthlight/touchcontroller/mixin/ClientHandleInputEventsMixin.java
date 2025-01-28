package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.fifthlight.touchcontroller.layout.ContextResult;
import top.fifthlight.touchcontroller.layout.ContextStatus;
import top.fifthlight.touchcontroller.model.ControllerHudModel;

@Mixin(Minecraft.class)
public abstract class ClientHandleInputEventsMixin {
    @Shadow
    @Final
    public GameSettings options;

    @Redirect(
            method = "handleKeybinds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/settings/KeyBinding;consumeClick()Z"
            )
    )
    private boolean wasPressed(KeyBinding instance) {
        ControllerHudModel controllerHudModel = KoinJavaComponent.get(ControllerHudModel.class);
        ContextStatus status = controllerHudModel.getStatus();
        if (instance == options.keyAttack) {
            return instance.consumeClick() || status.getAttack().wasPressed();
        } else if (instance == options.keyUse) {
            return instance.consumeClick() || status.getItemUse().wasPressed();
        } else if (instance == options.keyInventory) {
            return instance.consumeClick() || status.getOpenInventory().wasPressed();
        } else if (instance == options.keySwapOffhand) {
            return instance.consumeClick() || status.getSwapHands().wasPressed();
        } else {
            return instance.consumeClick();
        }
    }

    @Redirect(
            method = "handleKeybinds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/settings/KeyBinding;isDown()Z"
            )
    )
    private boolean isPressed(KeyBinding instance) {
        ControllerHudModel controllerHudModel = KoinJavaComponent.get(ControllerHudModel.class);
        ContextResult result = controllerHudModel.getResult();
        ContextStatus status = controllerHudModel.getStatus();
        if (instance == options.keyAttack) {
            return instance.isDown() || status.getAttack().isPressed();
        } else if (instance == options.keyUse) {
            return instance.isDown() || status.getItemUse().isPressed();
        } else if (instance == options.keyInventory) {
            return instance.isDown() || status.getOpenInventory().isPressed();
        } else if (instance == options.keySwapOffhand) {
            return instance.isDown() || status.getSwapHands().isPressed();
        } else if (instance == options.keySprint) {
            return instance.isDown() || result.getSprint() || status.getSprintLocked();
        } else {
            return instance.isDown();
        }
    }
}
