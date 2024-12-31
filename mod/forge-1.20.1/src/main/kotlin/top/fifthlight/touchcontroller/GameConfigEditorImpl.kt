package top.fifthlight.touchcontroller

import net.minecraft.client.Minecraft
import top.fifthlight.touchcontroller.config.GameConfigEditor

object GameConfigEditorImpl : GameConfigEditor {
    override fun enableAutoJump() {
        val client = Minecraft.getInstance()
        val options = client.options
        options.autoJump().set(true)
        options.save()
    }
}