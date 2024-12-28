package top.fifthlight.touchcontroller.event

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.config.TouchControllerConfigHolder
import top.fifthlight.touchcontroller.model.ControllerHudModel

object BlockBreakEvents : KoinComponent {
    private val controllerHudModel: ControllerHudModel by inject()
    private val configHolder: TouchControllerConfigHolder by inject()

    fun afterBlockBreak() {
        if (configHolder.config.value.vibration) {
            controllerHudModel.status.vibrate = true
        }
    }
}