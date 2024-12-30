package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerInteractionManager.class)
public interface ClientPlayerInteractionManagerInvoker {
    @Accessor("currentBreakingProgress")
    float getCurrentBreakingProgress();

    @Invoker("syncSelectedSlot")
    void callSyncSelectedSlot();
}
