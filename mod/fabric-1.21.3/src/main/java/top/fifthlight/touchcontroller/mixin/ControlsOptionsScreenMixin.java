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
import top.fifthlight.touchcontroller.assets.Texts;
import top.fifthlight.touchcontroller.ui.screen.config.ConfigScreenGetter;

@Mixin(ControlsOptionsScreen.class)
public abstract class ControlsOptionsScreenMixin {
    @Inject(at = @At("TAIL"), method = "addOptions")
    protected void addOptions(CallbackInfo ci) {
        var client = MinecraftClient.getInstance();
        var screen = (ControlsOptionsScreen) (Object) this;
        var body = ((GameOptionsScreenAccessor) this).body();
        var getter = ConfigScreenGetter.INSTANCE;
        body.addWidgetEntry(
                ButtonWidget.builder(
                        Text.of(Texts.INSTANCE.getSCREEN_OPTIONS().toString()),
                        btn -> {
                            client.setScreen((Screen) getter.getScreen(screen));
                        }
                ).build(), null
        );
    }
}