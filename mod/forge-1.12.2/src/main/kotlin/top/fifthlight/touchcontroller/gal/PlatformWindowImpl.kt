package top.fifthlight.touchcontroller.gal

import org.slf4j.LoggerFactory

object PlatformWindowImpl : PlatformWindow {
    private val logger = LoggerFactory.getLogger(PlatformWindowImpl::class.java)

    override fun getWin32Handle(): Long {
        fun cleanroom(): Long? {
            val displayClass = runCatching {
                Class.forName("org.lwjgl.opengl.Display")
            }.getOrNull() ?: return null

            val windowHandle: Long? = displayClass.methods?.firstOrNull {
                it.name == "getWindow"
            }?.invoke(null) as? Long

            val glfwNativeClass = runCatching {
                Class.forName("org.lwjgl3.glfw.GLFWNativeWin32")
            }.getOrNull() ?: return null

            return glfwNativeClass.methods?.firstOrNull {
                it.name == "glfwGetWin32Window"
            }?.invoke(null, windowHandle) as Long
        }

        fun plainForge(): Long? {
            val displayClass = runCatching {
                Class.forName("org.lwjgl.opengl.Display")
            }.getOrNull() ?: return null

            val displayImpl: Any = displayClass.declaredMethods?.firstOrNull {
                it.name == "getImplementation"
            }?.run {
                isAccessible = true
                invoke(null)
            } ?: return null

            return displayImpl.javaClass.declaredMethods.firstOrNull {
                it.name == "getHwnd"
            }?.run {
                isAccessible = true
                invoke(displayImpl) as? Long
            }
        }

        try {
            val handle = cleanroom() ?: plainForge() ?: error("Cleanroom and plain Forge failed")
            return handle
        } catch (ex: Exception) {
            logger.error("Failed to get window handle", ex)
            throw ex
        }
    }
}