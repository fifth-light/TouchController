package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.fifthlight.touchcontroller.model.ControllerHudModel;

@Mixin(Minecraft.class)
public abstract class ClientHandleInputEventsMixin {
    @Shadow
    @Final
    public Options options;

    @Redirect(
            method = "handleKeybinds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z"
            )
    )
    private boolean consumeClick(KeyMapping instance) {
        var controllerHudModel = (ControllerHudModel) KoinJavaComponent.get(ControllerHudModel.class);
        var status = controllerHudModel.getStatus();
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
                    target = "Lnet/minecraft/client/KeyMapping;isDown()Z"
            )
    )
    private boolean isPressed(KeyMapping instance) {
        var controllerHudModel = (ControllerHudModel) KoinJavaComponent.get(ControllerHudModel.class);
        var status = controllerHudModel.getStatus();
        if (instance == options.keyAttack) {
            return instance.isDown() || status.getAttack().isPressed();
        } else if (instance == options.keyUse) {
            return instance.isDown() || status.getItemUse().isPressed();
        } else if (instance == options.keyInventory) {
            return instance.isDown() || status.getOpenInventory().isPressed();
        } else if (instance == options.keySwapOffhand) {
            return instance.isDown() || status.getSwapHands().isPressed();
        } else {
            return instance.isDown();
        }
    }
}
