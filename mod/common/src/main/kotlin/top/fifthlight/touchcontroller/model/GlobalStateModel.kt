package top.fifthlight.touchcontroller.model

import top.fifthlight.touchcontroller.state.GlobalState

interface GlobalStateModel {
    val state: GlobalState

    fun update()
}
