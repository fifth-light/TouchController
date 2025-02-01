@file:Suppress("unused")

package top.fifthlight.touchcontroller.helper

import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.config.GlobalConfigHolder
import top.fifthlight.touchcontroller.gal.KeyBindingHandlerImpl
import top.fifthlight.touchcontroller.model.ControllerHudModel

object KeyBindingHelper : KoinComponent {
    private val controllerHudModel: ControllerHudModel by inject()
    private val configHolder: GlobalConfigHolder by inject()
    private val client = Minecraft.getMinecraft()

    @JvmStatic
    fun isKeyDown(original: Boolean, keyBinding: KeyBinding) = original || KeyBindingHandlerImpl.isDown(keyBinding)

    @JvmStatic
    fun doDisableKey(keyBinding: KeyBinding): Boolean {
        var config = configHolder.config.value

        if (keyBinding == client.gameSettings.keyBindAttack || keyBinding == client.gameSettings.keyBindUseItem) {
            return config.disableMouseClick || config.enableTouchEmulation
        }

        for (i in 0 until 9) {
            if (keyBinding == client.gameSettings.keyBindsHotbar[i]) {
                return config.disableHotBarKey
            }
        }

        return false
    }
}