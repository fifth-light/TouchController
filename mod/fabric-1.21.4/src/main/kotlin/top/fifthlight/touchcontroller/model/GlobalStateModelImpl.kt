package top.fifthlight.touchcontroller.model

import net.minecraft.client.MinecraftClient
import top.fifthlight.touchcontroller.ext.scaledSize
import top.fifthlight.touchcontroller.state.GlobalState

class GlobalStateModelImpl : GlobalStateModel {
    override var state: GlobalState = GlobalState()
        private set

    override fun update() {
        val client = MinecraftClient.getInstance()
        state = state.copy(
            inGame = client.currentScreen == null,
            windowSize = client.window.scaledSize
        )
    }
}