package top.fifthlight.touchcontroller.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.KeyMappingLookup;
import org.koin.java.KoinJavaComponent;
import org.koin.mp.KoinPlatform;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.fifthlight.touchcontroller.config.GlobalConfigHolder;
import top.fifthlight.touchcontroller.gal.KeyBindingHandlerImpl;
import top.fifthlight.touchcontroller.helper.ClickableKeyBinding;

import java.util.List;

@Mixin(KeyMapping.class)
public abstract class KeyBindingMixin implements ClickableKeyBinding {
    @Shadow
    @Final
    private static KeyMappingLookup MAP;

    @Shadow
    private int clickCount;

    @Unique
    private static boolean touchController$doCancelKey(InputConstants.Key key) {
        if (KoinPlatform.INSTANCE.getKoinOrNull() == null) {
            return false;
        }
        var configHolder = (GlobalConfigHolder) KoinJavaComponent.getOrNull(GlobalConfigHolder.class);
        if (configHolder == null) {
            return false;
        }
        var config = configHolder.getConfig().getValue();

        var client = Minecraft.getInstance();
        List<KeyMapping> keyBindings = MAP.getAll(key);

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
    private static void onKeyPressed(InputConstants.Key key, CallbackInfo info) {
        if (touchController$doCancelKey(key)) {
            info.cancel();
        }
    }

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressed(InputConstants.Key key, boolean pHeld, CallbackInfo info) {
        if (touchController$doCancelKey(key)) {
            info.cancel();
        }
    }

    @Override
    public void touchController$click() {
        clickCount++;
    }

    @Override
    public int touchController$getClickCount() {
        return clickCount;
    }

    @Inject(
            method = "isDown()Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void overrideIsDown(CallbackInfoReturnable<Boolean> info) {
        if (KeyBindingHandlerImpl.INSTANCE.isDown((KeyMapping) (Object) this)) {
            info.setReturnValue(true);
        }
    }
}