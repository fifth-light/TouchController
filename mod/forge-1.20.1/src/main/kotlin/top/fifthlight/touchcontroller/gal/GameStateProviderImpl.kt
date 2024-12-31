package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft

object GameStateProviderImpl : GameStateProvider {
    private val client = Minecraft.getInstance()

    override fun currentState(): GameState = GameState(
        inGame = client.player != null,
        inGui = client.screen != null,
    )
}