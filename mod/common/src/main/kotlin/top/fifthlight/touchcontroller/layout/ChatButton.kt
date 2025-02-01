package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.ChatButton

fun Context.ChatButton(config: ChatButton) {
    val (newClick) = Button(id = "chat") {
        if (config.classic) {
            Texture(texture = Textures.GUI_CHAT_CHAT_CLASSIC)
        } else {
            Texture(texture = Textures.GUI_CHAT_CHAT)
        }
    }

    if (newClick) {
        result.chat = true
    }
}