@file:Suppress("unused")

package top.fifthlight.touchcontroller.helper

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.config.GlobalConfigHolder

object EntityRendererHelper : KoinComponent {
    private val globalConfigHolder: GlobalConfigHolder by inject()

    @JvmStatic
    fun doDisableMouseDirection(): Boolean {
        globalConfigHolder
        var config = globalConfigHolder.config.value
        return config.disableMouseMove
    }
}