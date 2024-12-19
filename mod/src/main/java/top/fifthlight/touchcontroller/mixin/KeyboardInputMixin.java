package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.event.KeyboardInputEvents;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin {
    @Inject(
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/input/KeyboardInput;movementSideways:F",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            ),
            method = "tick"
    )
    private void tick(boolean slowDown, float slowDownFactor, CallbackInfo info) {
        KeyboardInputEvents.INSTANCE.getEND_INPUT_TICK().invoker().onEndTick((KeyboardInput) (Object) this);
    }
}
