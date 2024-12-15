package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.koin.java.KoinJavaComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.fifthlight.touchcontroller.model.ControllerHudModel;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    /// Because Minecraft Java version requires you to stand on ground to trigger sprint on double-clicking forward key,
    /// this method change the on ground logic to relax this requirement when using touch input.
    @Redirect(
            method = "tickMovement()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;isOnGround()Z",
                    ordinal = 0
            )
    )
    public boolean redirectIsOnGround(ClientPlayerEntity instance) {
        var controllerHudModel = (ControllerHudModel) KoinJavaComponent.get(ControllerHudModel.class);
        var contextResult = controllerHudModel.getResult();
        if (contextResult.getForward() != 0) {
            return true;
        } else {
            return instance.isOnGround();
        }
    }
}