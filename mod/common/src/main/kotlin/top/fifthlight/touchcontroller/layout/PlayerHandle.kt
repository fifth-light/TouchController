package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.config.TouchControllerConfig

interface PlayerHandle {
    /**/
    fun haveUsableItemsOnHand(config: TouchControllerConfig): Boolean
}

interface PlayerHandleFactory {
    fun getPlayerHandle(): PlayerHandle?
}