package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyBindingMap;
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

import java.util.List;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Shadow
    @Final
    private static KeyBindingMap MAP;

    @Unique
    private static boolean touchController$doCancelKey(InputMappings.Input key) {
        GlobalConfigHolder configHolder = KoinJavaComponent.getOrNull(GlobalConfigHolder.class);
        if (configHolder == null) {
            return false;
        }
        GlobalConfig config = configHolder.getConfig().getValue();

        Minecraft client = Minecraft.getInstance();
        List<KeyBinding> keyBindings = MAP.lookupAll(key);

        if (keyBindings.contains(client.options.keyAttack) || keyBindings.contains(client.options.keyUse)) {
            return config.getDisableMouseClick() || config.getEnableTouchEmulation();
        }

        for (int i = 0; i < 9; i++) {
            if (keyBindings.contains(client.options.keyHotbarSlots[i])) {
                return config.getDisableHotBarKey();
            }
        }

        return false;
    }

    @Inject(method = "click", at = @At("HEAD"), cancellable = true)
    private static void click(InputMappings.Input key, CallbackInfo info) {
        if (touchController$doCancelKey(key)) {
            info.cancel();
        }
    }

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private static void set(InputMappings.Input key, boolean pHeld, CallbackInfo info) {
        if (touchController$doCancelKey(key)) {
            info.cancel();
        }
    }
}