@file:Suppress("unused")

package top.fifthlight.touchcontroller.helper

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.config.GlobalConfigHolder

object MouseHelperHelper : KoinComponent {
    private val configHolder: GlobalConfigHolder by inject()

    @JvmStatic
    fun doDisableMouseGrab(): Boolean {
        val config = configHolder.config.value
        return config.disableMouseLock
    }
}
