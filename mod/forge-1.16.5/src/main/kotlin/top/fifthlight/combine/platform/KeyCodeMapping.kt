package top.fifthlight.combine.platform

import org.lwjgl.glfw.GLFW
import top.fifthlight.combine.input.key.Key
import top.fifthlight.combine.input.key.KeyModifier

fun mapKeyCode(code: Int) = when (code) {
    GLFW.GLFW_KEY_BACKSPACE -> Key.BACKSPACE
    GLFW.GLFW_KEY_ENTER -> Key.ENTER
    GLFW.GLFW_KEY_HOME -> Key.HOME
    GLFW.GLFW_KEY_END -> Key.END
    GLFW.GLFW_KEY_PAGE_UP -> Key.PAGE_UP
    GLFW.GLFW_KEY_PAGE_DOWN -> Key.PAGE_DOWN
    GLFW.GLFW_KEY_DELETE -> Key.DELETE
    GLFW.GLFW_KEY_LEFT -> Key.ARROW_LEFT
    GLFW.GLFW_KEY_UP -> Key.ARROW_UP
    GLFW.GLFW_KEY_RIGHT -> Key.ARROW_RIGHT
    GLFW.GLFW_KEY_DOWN -> Key.ARROW_DOWN
    else -> Key.UNKNOWN
}

fun mapModifier(code: Int) = KeyModifier(
    shift = (code and GLFW.GLFW_MOD_SHIFT) != 0,
    control = (code and GLFW.GLFW_MOD_CONTROL) != 0,
    meta = (code and GLFW.GLFW_MOD_ALT) != 0,
)