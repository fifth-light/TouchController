package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
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
        MinecraftClient client = MinecraftClient.getInstance();
        ControlsOptionsScreen screen = (ControlsOptionsScreen) (Object) this;

        ButtonWidget mouseSettingsButton = (ButtonWidget) screen.children().get(0);
        ControlsListWidget controlsListWidget = screen.children().stream()
                .filter(ControlsListWidget.class::isInstance)
                .map(ControlsListWidget.class::cast)
                .findFirst()
                .get();
        controlsListWidget.updateSize(screen.width + 45, screen.height, 67, screen.height - 32);

        ConfigScreenGetter getter = ConfigScreenGetter.INSTANCE;
        Text buttonText = (Text) getter.getText();
        ButtonWidget widget = new ButtonWidget(mouseSettingsButton.x, mouseSettingsButton.y + 24, 150, 20, buttonText, btn -> client.openScreen((Screen) getter.getScreen(screen)));
        addButton(widget);
    }
}