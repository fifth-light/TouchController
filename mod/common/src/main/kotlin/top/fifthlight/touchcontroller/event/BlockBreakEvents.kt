package top.fifthlight.touchcontroller.event

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.config.GlobalConfigHolder
import top.fifthlight.touchcontroller.model.ControllerHudModel

object BlockBreakEvents : KoinComponent {
    private val controllerHudModel: ControllerHudModel by inject()
    private val configHolder: GlobalConfigHolder by inject()

    fun afterBlockBreak() {
        if (configHolder.config.value.vibration) {
            controllerHudModel.status.vibrate = true
        }
    }
}