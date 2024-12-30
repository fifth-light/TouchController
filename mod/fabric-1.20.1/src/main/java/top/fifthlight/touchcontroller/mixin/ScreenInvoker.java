package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
interface ScreenInvoker {
    @Invoker
    <T extends Element & Drawable> T invokeAddDrawableChild(T drawableElement);
}