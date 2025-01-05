package top.fifthlight.combine.input.key

import androidx.compose.runtime.Immutable

enum class Key {
    BACKSPACE,
    ENTER,
    HOME,
    END,
    PAGE_UP,
    PAGE_DOWN,
    DELETE,
    ARROW_LEFT,
    ARROW_UP,
    ARROW_RIGHT,
    ARROW_DOWN,
    A,
    B,
    C,
    D,
    E,
    F,
    G,
    H,
    I,
    J,
    K,
    L,
    M,
    N,
    O,
    P,
    Q,
    R,
    S,
    T,
    U,
    V,
    W,
    X,
    Y,
    Z,
    NUM_0,
    NUM_1,
    NUM_2,
    NUM_3,
    NUM_4,
    NUM_5,
    NUM_6,
    NUM_7,
    NUM_8,
    NUM_9,
    UNKNOWN,
}

@Immutable
data class KeyModifier(
    val shift: Boolean = false,
    val control: Boolean = false,
    val meta: Boolean = false,
) {
    val onlyShift
        get() = shift && !control && !meta

    val onlyControl
        get() = !shift && control && !meta

    val onlyMeta
        get() = !shift && !control && meta

    val empty
        get() = !shift && !control && !meta
}

@Immutable
data class KeyEvent(
    val key: Key,
    val pressed: Boolean,
    val modifier: KeyModifier,
)
