package top.fifthlight.touchcontroller

import net.minecraft.client.MinecraftClient
import top.fifthlight.touchcontroller.config.GameConfigEditor

object GameConfigEditorImpl : GameConfigEditor {
    override fun enableAutoJump() {
        val client = MinecraftClient.getInstance()
        val options = client.options
        options.autoJump.value = true
        options.write()
    }
}