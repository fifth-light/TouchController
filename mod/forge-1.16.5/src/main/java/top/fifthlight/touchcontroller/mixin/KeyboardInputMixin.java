package top.fifthlight.touchcontroller.mixin;

import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.event.KeyboardInputEvents;

@Mixin(MovementInputFromOptions.class)
public abstract class KeyboardInputMixin {
    @Inject(
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/util/MovementInputFromOptions;shiftKeyDown:Z",
                    shift = At.Shift.AFTER
            ),
            method = "tick"
    )
    private void tick(boolean slowDown, CallbackInfo ci) {
        KeyboardInputEvents.INSTANCE.onEndTick((MovementInputFromOptions) (Object) this);
    }
}
