package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft

object GameStateProviderImpl : GameStateProvider {
    private val client = Minecraft.getMinecraft()

    override fun currentState(): GameState = GameState(
        inGame = client.player != null,
        inGui = client.currentScreen != null,
    )
}