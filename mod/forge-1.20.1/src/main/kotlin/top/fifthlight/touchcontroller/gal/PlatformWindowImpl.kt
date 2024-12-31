package top.fifthlight.touchcontroller.gal

import com.mojang.blaze3d.platform.Window
import org.lwjgl.glfw.GLFWNativeWin32

@JvmInline
value class PlatformWindowImpl(private val inner: Window) : PlatformWindow {
    override fun getWin32Handle(): Long {
        val glfwHandle = inner.window
        return GLFWNativeWin32.glfwGetWin32Window(glfwHandle)
    }
}