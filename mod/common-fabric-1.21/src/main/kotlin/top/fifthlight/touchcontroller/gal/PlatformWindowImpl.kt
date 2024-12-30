package top.fifthlight.touchcontroller.gal

import net.minecraft.client.util.Window
import org.lwjgl.glfw.GLFWNativeWin32

@JvmInline
value class PlatformWindowImpl(private val inner: Window) : PlatformWindow {
    override fun getWin32Handle(): Long {
        val glfwHandle = inner.handle
        return GLFWNativeWin32.glfwGetWin32Window(glfwHandle)
    }
}