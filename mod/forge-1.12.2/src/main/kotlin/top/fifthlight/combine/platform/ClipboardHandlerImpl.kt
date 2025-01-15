package top.fifthlight.combine.platform

import net.minecraft.client.gui.GuiScreen
import top.fifthlight.combine.input.input.ClipboardHandler

object ClipboardHandlerImpl : ClipboardHandler {
    override var text: String
        get() = GuiScreen.getClipboardString()
        set(value) {
            GuiScreen.setClipboardString(value)
        }
}
