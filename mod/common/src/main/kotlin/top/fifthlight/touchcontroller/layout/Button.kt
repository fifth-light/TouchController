package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.state.PointerState

data class ButtonResult(
    val newPointer: Boolean = false,
    val clicked: Boolean = false,
    val release: Boolean = false
)

fun Context.SwipeButton(
    id: String,
    content: Context.(clicked: Boolean) -> Unit,
): ButtonResult {
    var newPointer = false
    var clicked = false
    var release = false
    for (pointer in pointers.values) {
        when (val state = pointer.state) {
            PointerState.New -> {
                if (pointer.inRect(size)) {
                    pointer.state = PointerState.SwipeButton(id)
                    newPointer = true
                    clicked = true
                }
            }

            is PointerState.SwipeButton -> {
                if (pointer.inRect(size)) {
                    clicked = true
                }
            }

            is PointerState.Released -> {
                val previousState = state.previousState
                if (previousState is PointerState.SwipeButton && previousState.id == id) {
                    release = true
                }
            }

            else -> {}
        }
    }
    content(clicked)
    return ButtonResult(
        newPointer = newPointer,
        clicked = clicked,
        release = release
    )
}

fun Context.Button(
    id: String,
    content: Context.(clicked: Boolean) -> Unit,
): ButtonResult {
    var newPointer = false
    var clicked = false
    var release = false
    for (pointer in pointers.values) {
        when (val state = pointer.state) {
            PointerState.New -> {
                if (pointer.inRect(size)) {
                    pointer.state = PointerState.Button(id)
                    newPointer = true
                    clicked = true
                }
            }

            is PointerState.Button -> {
                if (pointer.inRect(size) && state.id == id) {
                    clicked = true
                }
            }

            is PointerState.Released -> {
                val previousState = state.previousState
                if (previousState is PointerState.Button && previousState.id == id) {
                    release = true
                }
            }

            else -> {}
        }
    }

    content(clicked)
    return ButtonResult(
        newPointer = newPointer,
        clicked = clicked,
        release = release
    )
}