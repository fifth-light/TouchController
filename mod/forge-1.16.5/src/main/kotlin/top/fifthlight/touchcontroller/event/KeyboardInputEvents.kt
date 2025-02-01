package top.fifthlight.touchcontroller.event

import net.minecraft.client.Minecraft
import net.minecraft.util.MovementInputFromOptions
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.model.ControllerHudModel

object KeyboardInputEvents : KoinComponent {
    private val controllerHudModel: ControllerHudModel by inject()

    fun onEndTick(input: MovementInputFromOptions) {
        val client = Minecraft.getInstance()
        if (client.screen != null) {
            return
        }

        val result = controllerHudModel.result

        input.forwardImpulse += result.forward
        input.leftImpulse += result.left
        input.forwardImpulse = input.forwardImpulse.coerceIn(-1f, 1f)
        input.leftImpulse = input.leftImpulse.coerceIn(-1f, 1f)
        input.up = input.up || result.forward > 0.5f || (result.boatLeft && result.boatRight)
        input.down = input.down || result.forward < -0.5f
        input.left = input.left || result.left > 0.5f || (!result.boatLeft && result.boatRight)
        input.right = input.right || result.left < -0.5f || (result.boatLeft && !result.boatRight)
    }
}
