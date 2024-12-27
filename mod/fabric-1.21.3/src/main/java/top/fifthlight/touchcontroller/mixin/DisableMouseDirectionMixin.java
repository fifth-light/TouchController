package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.Mouse;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.config.TouchControllerConfigHolder;

@Mixin(Mouse.class)
abstract class DisableMouseDirectionMixin {
    @Inject(
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/MinecraftClient;player:Lnet/minecraft/client/network/ClientPlayerEntity;",
                    ordinal = 1
            ),
            method = "updateMouse",
            cancellable = true
    )
    private void updateMouse(double timeDelta, CallbackInfo ci) {
        var configHolder = (TouchControllerConfigHolder) KoinJavaComponent.getOrNull(TouchControllerConfigHolder.class);
        if (configHolder == null) {
            return;
        }
        var config = configHolder.getConfig().getValue();
        if (config.getDisableMouseMove()) {
            ci.cancel();
        }
    }
}