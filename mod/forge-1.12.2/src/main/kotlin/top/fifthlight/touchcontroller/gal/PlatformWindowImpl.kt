package top.fifthlight.touchcontroller.gal

import org.lwjgl.opengl.Display

object PlatformWindowImpl : PlatformWindow {
    private val displayImpl: Any = Display::class.java.declaredMethods.first {
        it.name == "getImplementation"
    }.run {
        isAccessible = true
        invoke(null)
    }

    override fun getWin32Handle(): Long = displayImpl.javaClass.declaredMethods.first {
        it.name == "getHwnd"
    }.run {
        isAccessible = true
        invoke(displayImpl) as Long
    }
}