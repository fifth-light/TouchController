package top.fifthlight.touchcontroller.gal

import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import top.fifthlight.touchcontroller.helper.ClickableKeyBinding

private fun KeyMapping.click() {
    (this as ClickableKeyBinding).`touchController$click`()
}

private fun KeyMapping.getClickCount() = (this as ClickableKeyBinding).`touchController$getClickCount`()

private class KeyBindingStateImpl(
    private val keyBinding: KeyMapping,
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
        keyBinding.click()
    }

    override fun haveClickCount(): Boolean = keyBinding.getClickCount() > 0

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
    private val client = Minecraft.getInstance()
    private val options = client.options
    private val state = mutableMapOf<KeyMapping, KeyBindingStateImpl>()

    private fun KeyBindingType.toMinecraft() = when (this) {
        KeyBindingType.ATTACK -> options.keyAttack
        KeyBindingType.USE -> options.keyUse
        KeyBindingType.INVENTORY -> options.keyInventory
        KeyBindingType.SWAP_HANDS -> options.keySwapOffhand
        KeyBindingType.SNEAK -> options.keyShift
        KeyBindingType.SPRINT -> options.keySprint
        KeyBindingType.JUMP -> options.keyJump
    }

    fun isDown(key: KeyMapping) = state[key]?.let { it.clicked || it.locked } == true

    private fun getState(key: KeyMapping) = state.getOrPut(key) {
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
