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
            at = @At("TAIL"),
            method = "tick"
    )
    private void tick(CallbackInfo info) {
        KeyboardInputEvents.INSTANCE.getEND_INPUT_TICK().invoker().onEndTick((KeyboardInput) (Object) this);
    }
}
