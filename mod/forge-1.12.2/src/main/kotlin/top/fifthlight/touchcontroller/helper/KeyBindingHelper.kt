@file:Suppress("unused")

package top.fifthlight.touchcontroller.helper

import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.config.GlobalConfigHolder
import top.fifthlight.touchcontroller.model.ControllerHudModel

object KeyBindingHelper : KoinComponent {
    private val controllerHudModel: ControllerHudModel by inject()
    private val configHolder: GlobalConfigHolder by inject()
    private val client = Minecraft.getMinecraft()

    @JvmStatic
    fun isPressed(keyBinding: KeyBinding): Boolean {
        val status = controllerHudModel.status
        return when (keyBinding) {
            client.gameSettings.keyBindAttack -> status.attack.wasPressed()
            client.gameSettings.keyBindUseItem -> status.itemUse.wasPressed()
            client.gameSettings.keyBindInventory -> status.openInventory.wasPressed()
            client.gameSettings.keyBindSwapHands -> status.swapHands.wasPressed()
            else -> false
        }
    }

    @JvmStatic
    fun isKeyDown(original: Boolean, keyBinding: KeyBinding): Boolean {
        if (original) {
            return true
        }
        val status = controllerHudModel.status
        val result = controllerHudModel.result
        return when (keyBinding) {
            client.gameSettings.keyBindAttack -> status.attack.isPressed
            client.gameSettings.keyBindUseItem -> status.itemUse.isPressed
            client.gameSettings.keyBindInventory -> status.openInventory.isPressed
            client.gameSettings.keyBindSwapHands -> status.swapHands.isPressed
            client.gameSettings.keyBindSprint -> result.sprint || status.sprintLocked
            else -> false
        }
    }

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