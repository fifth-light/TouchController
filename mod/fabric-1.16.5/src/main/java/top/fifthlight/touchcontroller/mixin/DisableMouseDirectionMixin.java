package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.Mouse;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.config.GlobalConfig;
import top.fifthlight.touchcontroller.config.GlobalConfigHolder;

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
    private void updateMouse(CallbackInfo ci) {
        GlobalConfigHolder configHolder = KoinJavaComponent.getOrNull(GlobalConfigHolder.class);
        if (configHolder == null) {
            return;
        }
        GlobalConfig config = configHolder.getConfig().getValue();
        if (config.getDisableMouseMove()) {
            ci.cancel();
        }
    }
}