package top.fifthlight.touchcontroller.mixin;

import net.minecraft.client.util.InputUtil;
import org.koin.java.KoinJavaComponent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.fifthlight.touchcontroller.config.TouchControllerConfigHolder;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;

@Mixin(InputUtil.class)
public abstract class CursorLockMixin {
	@Shadow @Final public static int GLFW_CURSOR_NORMAL;

	@Inject(at = @At("TAIL"), method = "setCursorParameters")
	private static void setCursorParameters(long handler, int inputModeValue, double x, double y, CallbackInfo info) {
		var configHolder = (TouchControllerConfigHolder) KoinJavaComponent.getOrNull(TouchControllerConfigHolder.class);
		if (configHolder == null) {
			return;
		}
		var config = configHolder.getConfig().getValue();
		if (config.getDisableMouseLock()) {
			GLFW.glfwSetInputMode(handler, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		}
	}
}