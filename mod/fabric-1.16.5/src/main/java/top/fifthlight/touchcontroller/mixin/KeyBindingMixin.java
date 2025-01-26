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
import top.fifthlight.touchcontroller.config.GlobalConfig;
import top.fifthlight.touchcontroller.config.GlobalConfigHolder;

import java.util.Map;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Shadow
    @Final
    private static Map<InputUtil.Key, KeyBinding> keyToBindings;

    @Unique
    private static boolean doCancelKey(InputUtil.Key key) {
        GlobalConfigHolder configHolder = KoinJavaComponent.getOrNull(GlobalConfigHolder.class);
        if (configHolder == null) {
            return false;
        }
        GlobalConfig config = configHolder.getConfig().getValue();

        MinecraftClient client = MinecraftClient.getInstance();
        KeyBinding keyBinding = keyToBindings.get(key);

        if (keyBinding == client.options.keyAttack || keyBinding == client.options.keyUse) {
            return config.getDisableMouseClick() || config.getEnableTouchEmulation();
        }

        for (int i = 0; i < 9; i++) {
            if (client.options.keysHotbar[i] == keyBinding) {
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