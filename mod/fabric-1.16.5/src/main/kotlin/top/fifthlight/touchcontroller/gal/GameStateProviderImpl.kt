package top.fifthlight.touchcontroller.gal

import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.Perspective

object GameStateProviderImpl : GameStateProvider {
    private val client = MinecraftClient.getInstance()

    override fun currentState(): GameState = GameState(
        inGame = client.player != null,
        inGui = client.currentScreen != null,
        perspective = when (client.options.perspective) {
            Perspective.FIRST_PERSON -> CameraPerspective.FIRST_PERSON
            Perspective.THIRD_PERSON_BACK -> CameraPerspective.THIRD_PERSON_BACK
            Perspective.THIRD_PERSON_FRONT -> CameraPerspective.THIRD_PERSON_FRONT
        },
    )
}