package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface ClientOpenChatScreenInvoker {
    @Invoker("openChatScreen")
    void callOpenChatScreen(String text);
}
