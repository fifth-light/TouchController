package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultiPlayerGameMode.class)
public interface MultiPlayerGameModeInvoker {
    @Accessor("destroyProgress")
    float getDestroyProgress();

    @Invoker("ensureHasSentCarriedItem")
    void callEnsureHasSentCarriedItem();
}
