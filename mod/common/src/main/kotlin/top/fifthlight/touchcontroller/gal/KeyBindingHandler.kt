package top.fifthlight.touchcontroller.gal

enum class KeyBindingType {
    ATTACK,
    USE,
    INVENTORY,
    SWAP_HANDS,
    SNEAK,
    SPRINT,
    JUMP,
}

interface KeyBindingState {
    // Click for once. You probably don't want to use this as it only increases press count, without actually pressing
    // the button. If it causes problems, use clicked = true instead.
    fun click()

    fun haveClickCount(): Boolean

    // Click for one tick (client tick). It will be reset every tick.
    var clicked: Boolean

    // Lock between ticks. You can read value from this field to query lock state.
    var locked: Boolean

    companion object Empty : KeyBindingState {
        override fun click() {}
        override fun haveClickCount() = false
        override var clicked: Boolean
            get() = false
            set(_) {}
        override var locked: Boolean
            get() = false
            set(_) {}
    }
}

interface KeyBindingHandler {
    fun renderTick()
    fun clientTick()
    fun getState(type: KeyBindingType): KeyBindingState

    companion object Empty : KeyBindingHandler {
        override fun renderTick() {}
        override fun clientTick() {}
        override fun getState(type: KeyBindingType) = KeyBindingState.Empty
    }
}