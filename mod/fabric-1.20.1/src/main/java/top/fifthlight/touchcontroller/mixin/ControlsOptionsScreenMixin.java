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
public abstract class ControlsOptionsScreenMixin extends Screen {
    protected ControlsOptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init")
    protected void init(CallbackInfo ci) {
        var client = MinecraftClient.getInstance();
        var screen = (ControlsOptionsScreen) (Object) this;

        var doneButton = (ButtonWidget) screen.children().get(screen.children().size() - 1);
        doneButton.setPosition(doneButton.getX(), doneButton.getY() + 24);

        var getter = ConfigScreenGetter.INSTANCE;
        addDrawableChild(
                ButtonWidget
                        .builder(
                                (Text) getter.getText(),
                                btn -> client.setScreen((Screen) getter.getScreen(screen))
                        )
                        .dimensions(screen.width / 2 - 155, screen.height / 6 + 60, 150, 20)
                        .build()
        );
    }
}