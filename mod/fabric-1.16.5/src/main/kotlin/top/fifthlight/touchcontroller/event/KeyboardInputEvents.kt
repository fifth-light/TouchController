package top.fifthlight.touchcontroller.event

import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.KeyboardInput
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.model.ControllerHudModel

object KeyboardInputEvents : KoinComponent {
    private val controllerHudModel: ControllerHudModel by inject()

    fun onEndTick(input: KeyboardInput) {
        val client = MinecraftClient.getInstance()
        if (client.currentScreen != null) {
            return
        }

        val result = controllerHudModel.result
        val status = controllerHudModel.status

        input.movementForward += result.forward
        input.movementSideways += result.left
        input.movementForward = input.movementForward.coerceIn(-1f, 1f)
        input.movementSideways = input.movementSideways.coerceIn(-1f, 1f)
        input.sneaking = input.sneaking || status.sneakLocked || result.sneak || status.sneaking
        input.jumping = input.jumping || status.jumping
        input.pressingForward = input.pressingForward || result.forward > 0.5f || (result.boatLeft && result.boatRight)
        input.pressingBack = input.pressingBack || result.forward < -0.5f
        input.pressingLeft = input.pressingLeft || result.left > 0.5f || (!result.boatLeft && result.boatRight)
        input.pressingRight = input.pressingRight || result.left < -0.5f || (result.boatLeft && !result.boatRight)

        status.sneaking = false
        status.jumping = false

        TickEvents.inputTick()
    }
}
