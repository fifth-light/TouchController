package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface ClientOpenChatScreenInvoker {
    @Invoker("openChatScreen")
    void callOpenChatScreen(String text);
}
