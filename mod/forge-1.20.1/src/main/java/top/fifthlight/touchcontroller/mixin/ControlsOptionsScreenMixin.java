package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.ui.screen.config.ConfigScreenGetter;

@Mixin(ControlsScreen.class)
public abstract class ControlsOptionsScreenMixin extends Screen {
    protected ControlsOptionsScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Inject(at = @At("TAIL"), method = "init")
    protected void init(CallbackInfo ci) {
        var client = Minecraft.getInstance();
        var screen = (ControlsScreen) (Object) this;

        var doneButton = (Button) screen.children().get(screen.children().size() - 1);
        doneButton.setPosition(doneButton.getX(), doneButton.getY() + 24);

        var getter = ConfigScreenGetter.INSTANCE;
        addRenderableWidget(
                Button
                        .builder(
                                (Component) getter.getText(),
                                btn -> client.setScreen((Screen) getter.getScreen(screen))
                        )
                        .bounds(screen.width / 2 - 155, screen.height / 6 + 60, 150, 20)
                        .build()
        );
    }
}