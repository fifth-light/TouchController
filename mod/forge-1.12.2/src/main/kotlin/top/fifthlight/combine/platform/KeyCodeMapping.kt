package top.fifthlight.combine.platform

import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import top.fifthlight.combine.input.key.Key
import top.fifthlight.combine.input.key.KeyModifier

fun mapKeyCode(code: Int) = when (code) {
    Keyboard.KEY_BACK -> Key.BACKSPACE
    Keyboard.KEY_RETURN -> Key.ENTER
    Keyboard.KEY_HOME -> Key.HOME
    Keyboard.KEY_END -> Key.END
    Keyboard.KEY_NEXT -> Key.PAGE_UP
    Keyboard.KEY_PRIOR -> Key.PAGE_DOWN
    Keyboard.KEY_DELETE -> Key.DELETE
    Keyboard.KEY_LEFT -> Key.ARROW_LEFT
    Keyboard.KEY_UP -> Key.ARROW_UP
    Keyboard.KEY_RIGHT -> Key.ARROW_RIGHT
    Keyboard.KEY_DOWN -> Key.ARROW_DOWN
    else -> Key.UNKNOWN
}

fun mapModifier() = KeyModifier(
    shift = GuiScreen.isShiftKeyDown(),
    control = GuiScreen.isCtrlKeyDown(),
    meta = GuiScreen.isAltKeyDown(),
)