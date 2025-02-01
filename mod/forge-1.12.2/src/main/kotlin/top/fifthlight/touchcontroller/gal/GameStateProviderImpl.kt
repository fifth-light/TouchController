package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft

object GameStateProviderImpl : GameStateProvider {
    private val client = Minecraft.getMinecraft()

    override fun currentState(): GameState = GameState(
        inGame = client.player != null,
        inGui = client.currentScreen != null,
        perspective = when (client.gameSettings.thirdPersonView) {
            0 -> CameraPerspective.FIRST_PERSON
            1 -> CameraPerspective.THIRD_PERSON_BACK
            2 -> CameraPerspective.THIRD_PERSON_FRONT
            // Unknown perspective
            else -> CameraPerspective.FIRST_PERSON
        },
    )
}