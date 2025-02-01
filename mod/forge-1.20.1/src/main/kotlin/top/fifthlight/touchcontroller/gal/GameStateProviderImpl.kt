package top.fifthlight.touchcontroller.gal

import net.minecraft.client.CameraType
import net.minecraft.client.Minecraft

object GameStateProviderImpl : GameStateProvider {
    private val client = Minecraft.getInstance()

    override fun currentState(): GameState = GameState(
        inGame = client.player != null,
        inGui = client.screen != null,
        perspective = when (client.options.cameraType) {
            CameraType.FIRST_PERSON -> CameraPerspective.FIRST_PERSON
            CameraType.THIRD_PERSON_BACK -> CameraPerspective.THIRD_PERSON_BACK
            CameraType.THIRD_PERSON_FRONT -> CameraPerspective.THIRD_PERSON_FRONT
        },
    )
}