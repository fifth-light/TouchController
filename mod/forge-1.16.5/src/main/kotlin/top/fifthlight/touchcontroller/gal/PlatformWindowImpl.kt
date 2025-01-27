package top.fifthlight.touchcontroller.gal

import net.minecraft.client.MainWindow
import org.lwjgl.glfw.GLFWNativeWin32

@JvmInline
value class PlatformWindowImpl(private val inner: MainWindow) : PlatformWindow {
    override fun getWin32Handle(): Long {
        val glfwHandle = inner.window
        return GLFWNativeWin32.glfwGetWin32Window(glfwHandle)
    }
}