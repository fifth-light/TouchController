package top.fifthlight.touchcontroller

import net.minecraft.client.Minecraft
import top.fifthlight.touchcontroller.config.GameConfigEditor

object GameConfigEditorImpl : GameConfigEditor {
    override fun enableAutoJump() {
        val client = Minecraft.getMinecraft()
        val options = client.gameSettings
        options.autoJump = true
        options.saveOptions()
    }
}