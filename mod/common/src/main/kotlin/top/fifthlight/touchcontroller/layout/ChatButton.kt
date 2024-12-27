package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.ChatButton

fun Context.ChatButton(config: ChatButton) {
    val (newClick) = Button(id = "chat") {
        if (config.classic) {
            Texture(id = Textures.GUI_CHAT_CHAT_CLASSIC)
        } else {
            Texture(id = Textures.GUI_CHAT_CHAT)
        }
    }

    result.chat = newClick
}