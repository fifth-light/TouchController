package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.multiplayer.PlayerController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerController.class)
public interface ClientPlayerInteractionManagerInvoker {
    @Accessor("destroyProgress")
    float getCurrentBreakingProgress();

    @Invoker("ensureHasSentCarriedItem")
    void callSyncSelectedSlot();
}
