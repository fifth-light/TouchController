@file:Suppress("unused")

package top.fifthlight.touchcontroller.helper

import net.minecraft.client.Minecraft
import net.minecraft.util.MovementInput
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.event.TickEvents
import top.fifthlight.touchcontroller.model.ControllerHudModel

object MovementInputFromOptionsHelper : KoinComponent {
    private val controllerHudModel: ControllerHudModel by inject()

    @JvmStatic
    fun onEndTick(input: MovementInput) {
        val client = Minecraft.getMinecraft()
        if (client.currentScreen != null) {
            return
        }

        val result = controllerHudModel.result
        val status = controllerHudModel.status

        input.moveForward += result.forward
        input.moveStrafe += result.left
        input.moveForward = input.moveForward.coerceIn(-1f, 1f)
        input.moveStrafe = input.moveStrafe.coerceIn(-1f, 1f)
        input.sneak = input.sneak || status.sneakLocked || result.sneak
        input.jump = input.jump || status.jumping
        input.forwardKeyDown = input.forwardKeyDown || result.forward > 0.5f
        input.backKeyDown = input.backKeyDown || result.forward < -0.5f
        input.leftKeyDown = input.leftKeyDown || result.left > 0.5f
        input.rightKeyDown = input.rightKeyDown || result.left < -0.5f
        status.jumping = false

        TickEvents.inputTick()
    }
}
