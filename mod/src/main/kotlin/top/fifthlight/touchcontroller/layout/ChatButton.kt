package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.asset.Textures
import top.fifthlight.touchcontroller.control.ChatButton

fun Context.ChatButton(config: ChatButton) {
    val (newClick) = Button(id = "chat") {
        if (config.classic) {
            Texture(id = Textures.CHAT_CLASSIC)
        } else {
            Texture(id = Textures.CHAT)
        }
    }

    result.chat = newClick
}