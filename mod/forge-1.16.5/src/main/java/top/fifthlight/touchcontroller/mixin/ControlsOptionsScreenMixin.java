package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.ui.screen.config.ConfigScreenGetter;

@Mixin(ControlsScreen.class)
public abstract class ControlsOptionsScreenMixin extends Screen {
    protected ControlsOptionsScreenMixin(ITextComponent title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init")
    protected void init(CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        ControlsScreen screen = (ControlsScreen) (Object) this;

        Button mouseSettingsButton = (Button) screen.children().get(0);
        KeyBindingList controlsListWidget = screen.children().stream()
                .filter(KeyBindingList.class::isInstance)
                .map(KeyBindingList.class::cast)
                .findFirst()
                .get();
        controlsListWidget.updateSize(screen.width + 45, screen.height, 67, screen.height - 32);

        ConfigScreenGetter getter = ConfigScreenGetter.INSTANCE;
        ITextComponent buttonText = (ITextComponent) getter.getText();
        Button widget = new Button(mouseSettingsButton.x, mouseSettingsButton.y + 24, 150, 20, buttonText, btn -> client.setScreen((Screen) getter.getScreen(screen)));
        addButton(widget);
    }
}