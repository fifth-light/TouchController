@file:Suppress("unused")

package top.fifthlight.touchcontroller.helper

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.model.ControllerHudModel

object EntityPlayerSPHelper : KoinComponent {
    private val controllerHudModel: ControllerHudModel by inject()

    @JvmStatic
    fun shouldReturnOnGround(onGround: Boolean): Boolean {
        if (onGround) {
            return true
        }
        return controllerHudModel.result.forward != 0f
    }
}