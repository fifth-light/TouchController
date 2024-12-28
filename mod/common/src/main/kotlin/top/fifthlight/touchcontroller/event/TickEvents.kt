package top.fifthlight.touchcontroller.event

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.model.ControllerHudModel

object TickEvents : KoinComponent {
    private val controllerHudModel: ControllerHudModel by inject()

    // Client side input tick, neither server tick nor client render tick
    fun inputTick() {
        controllerHudModel.timer.tick()
    }
}
