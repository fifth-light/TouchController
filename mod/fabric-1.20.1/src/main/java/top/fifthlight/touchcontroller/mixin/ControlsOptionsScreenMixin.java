package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.ui.screen.config.ConfigScreenGetter;

@Mixin(ControlsOptionsScreen.class)
public abstract class ControlsOptionsScreenMixin {
    @Inject(at = @At("TAIL"), method = "init")
    protected void init(CallbackInfo ci) {
        var client = MinecraftClient.getInstance();
        var screen = (ControlsOptionsScreen) (Object) this;
        var invoker = (ScreenInvoker) screen;
        var getter = ConfigScreenGetter.INSTANCE;
        invoker.invokeAddDrawableChild(
                ButtonWidget
                        .builder(
                                (Text) getter.getText(),
                                btn -> client.setScreen((Screen) getter.getScreen(screen))
                        )
                        .dimensions(0, 0, 150, 20)
                        .build()
        );
    }
}