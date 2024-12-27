package top.fifthlight.touchcontroller.handler

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Window
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lwjgl.glfw.GLFWNativeWin32
import org.slf4j.LoggerFactory
import top.fifthlight.touchcontroller.platform.PlatformHolder
import top.fifthlight.touchcontroller.platform.PlatformWindow

@JvmInline
private value class PlatformWindowImpl(private val inner: Window) : PlatformWindow {
    override fun getWin32Handle(): Long {
        val glfwHandle = inner.handle
        return GLFWNativeWin32.glfwGetWin32Window(glfwHandle)
    }
}

class ClientStartHandler : ClientLifecycleEvents.ClientStarted, KoinComponent {
    private val logger = LoggerFactory.getLogger(ClientStartHandler::class.java)

    private val platform: PlatformHolder by inject()

    override fun onClientStarted(client: MinecraftClient) {
        val platform = platform.platform ?: return
        val window = client.window
        platform.onWindowCreated(PlatformWindowImpl(window))
        logger.info("Called platform onWindowCreated")
    }
}
