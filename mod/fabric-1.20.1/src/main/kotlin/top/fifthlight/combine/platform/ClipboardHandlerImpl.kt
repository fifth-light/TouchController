package top.fifthlight.combine.platform

import net.minecraft.client.MinecraftClient
import top.fifthlight.combine.input.input.ClipboardHandler

object ClipboardHandlerImpl : ClipboardHandler {
    private val client by lazy { MinecraftClient.getInstance() }

    override var text: String
        get() = client.keyboard.clipboard
        set(value) {
            client.keyboard.clipboard = value
        }
}