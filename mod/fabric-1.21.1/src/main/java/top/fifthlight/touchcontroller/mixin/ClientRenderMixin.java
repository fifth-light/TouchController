package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.event.ClientRenderEvents;

@Mixin(MinecraftClient.class)
public abstract class ClientRenderMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void startRender(boolean tick, CallbackInfo ci) {
        ClientRenderEvents.INSTANCE.getSTART_TICK().invoker().onStartTick((MinecraftClient) (Object) this, tick);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void endRender(boolean tick, CallbackInfo ci) {
        ClientRenderEvents.INSTANCE.getEND_TICK().invoker().onEndTick((MinecraftClient) (Object) this, tick);
    }
}
