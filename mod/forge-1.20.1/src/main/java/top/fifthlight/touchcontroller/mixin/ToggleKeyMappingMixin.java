package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.ToggleKeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.fifthlight.touchcontroller.gal.KeyBindingHandlerImpl;
import top.fifthlight.touchcontroller.helper.ClickableKeyBinding;

@Mixin(ToggleKeyMapping.class)
public abstract class ToggleKeyMappingMixin implements ClickableKeyBinding {
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