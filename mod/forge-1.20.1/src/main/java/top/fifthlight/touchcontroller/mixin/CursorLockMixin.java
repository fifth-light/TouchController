package top.fifthlight.touchcontroller.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import org.koin.java.KoinJavaComponent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.config.GlobalConfigHolder;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;

@Mixin(InputConstants.class)
public abstract class CursorLockMixin {
    @Shadow
    @Final
    public static int CURSOR_NORMAL;

    @Inject(at = @At("TAIL"), method = "grabOrReleaseMouse")
    private static void grabOrReleaseMouse(long handler, int inputModeValue, double x, double y, CallbackInfo info) {
        var configHolder = (GlobalConfigHolder) KoinJavaComponent.getOrNull(GlobalConfigHolder.class);
        if (configHolder == null) {
            return;
        }
        var config = configHolder.getConfig().getValue();
        if (config.getDisableMouseLock()) {
            GLFW.glfwSetInputMode(handler, GLFW_CURSOR, CURSOR_NORMAL);
        }
    }
}