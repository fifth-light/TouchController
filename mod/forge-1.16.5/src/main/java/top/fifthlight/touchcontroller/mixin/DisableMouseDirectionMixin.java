package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.MouseHelper;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.config.GlobalConfig;
import top.fifthlight.touchcontroller.config.GlobalConfigHolder;

@Mixin(MouseHelper.class)
abstract class DisableMouseDirectionMixin {
    @Inject(
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/player/ClientPlayerEntity;",
                    ordinal = 1
            ),
            method = "turnPlayer",
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