package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.config.TouchControllerConfigHolder;

import java.util.Map;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Shadow
    @Final
    private static Map<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS;

    @Unique
    private static boolean doCancelKey(InputUtil.Key key) {
        var configHolder = (TouchControllerConfigHolder) KoinJavaComponent.getOrNull(TouchControllerConfigHolder.class);
        if (configHolder == null) {
            return false;
        }
        var config = configHolder.getConfig().getValue();

        var client = MinecraftClient.getInstance();
        KeyBinding keyBinding = KEY_TO_BINDINGS.get(key);

        if (keyBinding == client.options.attackKey || keyBinding == client.options.useKey) {
            return config.getDisableMouseClick() || config.getEnableTouchEmulation();
        }

        for (int i = 0; i < 9; i++) {
            if (client.options.hotbarKeys[i] == keyBinding) {
                return config.getDisableHotBarKey();
            }
        }

        return false;
    }

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo info) {
        if (doCancelKey(key)) {
            info.cancel();
        }
    }

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo info) {
        if (doCancelKey(key)) {
            info.cancel();
        }
    }
}