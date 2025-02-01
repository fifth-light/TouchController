package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft
import net.minecraft.client.settings.PointOfView

object GameStateProviderImpl : GameStateProvider {
    private val client = Minecraft.getInstance()

    override fun currentState(): GameState = GameState(
        inGame = client.player != null,
        inGui = client.screen != null,
        perspective = when (client.options.cameraType) {
            PointOfView.FIRST_PERSON -> CameraPerspective.FIRST_PERSON
            PointOfView.THIRD_PERSON_BACK -> CameraPerspective.THIRD_PERSON_BACK
            PointOfView.THIRD_PERSON_FRONT -> CameraPerspective.THIRD_PERSON_FRONT
        },
    )
}