package top.fifthlight.touchcontroller.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.config.GlobalConfigHolder;

import java.util.Map;

@Mixin(KeyMapping.class)
public abstract class KeyBindingMixin {
    @Shadow
    @Final
    private static Map<InputConstants.Key, KeyMapping> ALL;

    @Unique
    private static boolean doCancelKey(InputConstants.Key key) {
        var configHolder = (GlobalConfigHolder) KoinJavaComponent.getOrNull(GlobalConfigHolder.class);
        if (configHolder == null) {
            return false;
        }
        var config = configHolder.getConfig().getValue();

        var client = Minecraft.getInstance();
        KeyMapping keyBinding = ALL.get(key);

        if (keyBinding == client.options.keyAttack || keyBinding == client.options.keyUse) {
            return config.getDisableMouseClick() || config.getEnableTouchEmulation();
        }

        for (int i = 0; i < 9; i++) {
            if (client.options.keyHotbarSlots[i] == keyBinding) {
                return config.getDisableHotBarKey();
            }
        }

        return false;
    }

    @Inject(method = "click", at = @At("HEAD"), cancellable = true)
    private static void click(InputConstants.Key key, CallbackInfo info) {
        if (doCancelKey(key)) {
            info.cancel();
        }
    }

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private static void set(InputConstants.Key key, boolean pHeld, CallbackInfo info) {
        if (doCancelKey(key)) {
            info.cancel();
        }
    }
}