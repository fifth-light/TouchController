package top.fifthlight.touchcontroller.event

import net.minecraft.client.Minecraft
import net.minecraft.client.player.KeyboardInput
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.model.ControllerHudModel

object KeyboardInputEvents : KoinComponent {
    private val controllerHudModel: ControllerHudModel by inject()

    fun onEndTick(input: KeyboardInput) {
        val client = Minecraft.getInstance()
        if (client.screen != null) {
            return
        }

        val result = controllerHudModel.result
        val status = controllerHudModel.status

        input.forwardImpulse += result.forward
        input.leftImpulse += result.left
        input.forwardImpulse = input.forwardImpulse.coerceIn(-1f, 1f)
        input.leftImpulse = input.leftImpulse.coerceIn(-1f, 1f)
        input.shiftKeyDown = input.shiftKeyDown || status.sneakLocked || result.sneak || status.sneaking
        input.jumping = input.jumping || status.jumping
        input.up = input.up || result.forward > 0.5f
        input.down = input.down || result.forward < -0.5f
        input.left = input.left || result.left > 0.5f
        input.right = input.right || result.left < -0.5f

        status.sneaking = false
        status.jumping = false

        TickEvents.inputTick()
    }
}
