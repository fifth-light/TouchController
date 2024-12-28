package top.fifthlight.touchcontroller.event

import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.KeyboardInput
import net.minecraft.util.PlayerInput
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.model.ControllerHudModel

object KeyboardInputEvents : KoinComponent {
    private val controllerHudModel: ControllerHudModel by inject()
    private val client: MinecraftClient by inject()

    fun onEndTick(input: KeyboardInput) {
        if (client.currentScreen != null) {
            return
        }

        val result = controllerHudModel.result
        val status = controllerHudModel.status

        input.movementForward += result.forward
        input.movementSideways += result.left
        input.movementForward = input.movementForward.coerceIn(-1f, 1f)
        input.movementSideways = input.movementSideways.coerceIn(-1f, 1f)
        input.playerInput = PlayerInput(
            input.playerInput.forward() || result.forward > 0.5f,
            input.playerInput.backward() || result.forward < -0.5f,
            input.playerInput.left() || result.left > 0.5f,
            input.playerInput.right() || result.left < -0.5f,
            input.playerInput.jump() || status.jumping,
            input.playerInput.sneak() || status.sneakLocked || result.sneak,
            input.playerInput.sprint() || result.sprint
        )
        status.jumping = false

        TickEvents.inputTick()
    }
}
