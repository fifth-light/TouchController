package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.ToggleableKeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.fifthlight.touchcontroller.gal.KeyBindingHandlerImpl;
import top.fifthlight.touchcontroller.helper.ClickableKeyBinding;

@Mixin(ToggleableKeyBinding.class)
public abstract class ToggleKeyMappingMixin implements ClickableKeyBinding {
    @Inject(
            method = "isDown()Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void overrideIsDown(CallbackInfoReturnable<Boolean> info) {
        if (KeyBindingHandlerImpl.INSTANCE.isDown((KeyBinding) (Object) this)) {
            info.setReturnValue(true);
        }
    }
}