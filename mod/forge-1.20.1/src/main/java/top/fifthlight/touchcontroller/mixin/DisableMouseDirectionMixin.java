package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.MouseHandler;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.config.GlobalConfigHolder;

@Mixin(MouseHandler.class)
abstract class DisableMouseDirectionMixin {
    @Inject(
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/player/LocalPlayer;",
                    ordinal = 1
            ),
            method = "turnPlayer",
            cancellable = true
    )
    private void turnPlayer(CallbackInfo ci) {
        var configHolder = (GlobalConfigHolder) KoinJavaComponent.getOrNull(GlobalConfigHolder.class);
        if (configHolder == null) {
            return;
        }
        var config = configHolder.getConfig().getValue();
        if (config.getDisableMouseMove()) {
            ci.cancel();
        }
    }
}