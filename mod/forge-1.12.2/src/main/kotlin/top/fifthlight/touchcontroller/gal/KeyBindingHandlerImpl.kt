package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding

private class KeyBindingStateImpl(
    private val keyBinding: KeyBinding,
) : KeyBindingState {
    private var passedClientTick = false

    fun renderTick() {
        if (passedClientTick) {
            wasClicked = clicked || locked
            clicked = false
            passedClientTick = false
        }
    }

    fun clientTick() {
        passedClientTick = true
    }

    override fun click() {
        keyBinding.pressTime++
    }

    override fun haveClickCount(): Boolean = keyBinding.pressTime > 0

    private var wasClicked: Boolean = false

    override var clicked: Boolean = false
        set(value) {
            if (!locked && !wasClicked && !field && value) {
                click()
            }
            field = value
        }

    override var locked: Boolean = false
        set(value) {
            if (!clicked && !field && value) {
                click()
            }
            field = value
        }
}

object KeyBindingHandlerImpl : KeyBindingHandler {
    private val client = Minecraft.getMinecraft()
    private val options = client.gameSettings
    private val state = mutableMapOf<KeyBinding, KeyBindingStateImpl>()

    private fun KeyBindingType.toMinecraft() = when (this) {
        KeyBindingType.ATTACK -> options.keyBindAttack
        KeyBindingType.USE -> options.keyBindUseItem
        KeyBindingType.INVENTORY -> options.keyBindInventory
        KeyBindingType.SWAP_HANDS -> options.keyBindSwapHands
        KeyBindingType.SNEAK -> options.keyBindSneak
        KeyBindingType.SPRINT -> options.keyBindSprint
        KeyBindingType.JUMP -> options.keyBindJump
    }

    fun isDown(key: KeyBinding) = state[key]?.let { it.clicked || it.locked } == true

    private fun getState(key: KeyBinding) = state.getOrPut(key) {
        KeyBindingStateImpl(key)
    }

    override fun renderTick() {
        for (state in state.values) {
            state.renderTick()
        }
    }

    override fun clientTick() {
        for (state in state.values) {
            state.clientTick()
        }
    }

    override fun getState(type: KeyBindingType): KeyBindingState {
        return getState(type.toMinecraft())
    }
}
