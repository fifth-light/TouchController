package top.fifthlight.touchcontroller.event

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.gal.KeyBindingHandler
import top.fifthlight.touchcontroller.model.ControllerHudModel

object TickEvents : KoinComponent {
    private val controllerHudModel: ControllerHudModel by inject()
    private val keyBindingHandler: KeyBindingHandler by inject()

    // Client side tick, neither server tick nor client render tick
    fun clientTick() {
        controllerHudModel.timer.tick()
        keyBindingHandler.clientTick()
    }
}
