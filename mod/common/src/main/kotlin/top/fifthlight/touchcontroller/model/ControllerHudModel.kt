package top.fifthlight.touchcontroller.model

import org.koin.core.component.KoinComponent
import top.fifthlight.touchcontroller.layout.ContextCounter
import top.fifthlight.touchcontroller.layout.ContextResult
import top.fifthlight.touchcontroller.layout.ContextStatus
import top.fifthlight.touchcontroller.layout.DrawQueue

class ControllerHudModel : KoinComponent {
    var result = ContextResult()
    val status = ContextStatus()
    val timer = ContextCounter()
    var pendingDrawQueue: DrawQueue? = null
}
