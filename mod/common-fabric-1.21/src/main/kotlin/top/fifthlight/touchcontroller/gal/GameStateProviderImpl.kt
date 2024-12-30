package top.fifthlight.touchcontroller.gal

import net.minecraft.client.MinecraftClient

object GameStateProviderImpl : GameStateProvider {
    private val client = MinecraftClient.getInstance()

    override fun currentState(): GameState = GameState(
        inGame = client.player != null,
        inGui = client.currentScreen != null,
    )
}