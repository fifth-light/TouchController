package top.fifthlight.touchcontroller.event

import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.KeyboardInput
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
        input.sneaking = input.sneaking || status.sneakLocked || result.sneak
        input.jumping = input.jumping || status.jumping
        input.pressingForward = input.pressingForward || result.forward > 0.5f
        input.pressingBack = input.pressingBack || result.forward < -0.5f
        input.pressingLeft = input.pressingLeft || result.left > 0.5f
        input.pressingRight = input.pressingRight || result.left < -0.5f
        status.jumping = false

        TickEvents.inputTick()
    }
}
