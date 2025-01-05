package top.fifthlight.combine.input.focus

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import top.fifthlight.combine.node.LayoutNode

val LocalFocusManager = staticCompositionLocalOf<FocusManager> { error("No FocusManager in context") }

class FocusManager {
    private var _focusedNode = MutableStateFlow<LayoutNode?>(null)
    val focusedNode = _focusedNode.asStateFlow()

    fun requestFocus(node: LayoutNode) {
        if (!node.focusable) {
            return
        }
        _focusedNode.getAndUpdate {
            if (it != node) {
                it?.onFocusStateChanged(false)
            }
            node
        }
        node.onFocusStateChanged(true)
    }

    fun requestBlur() {
        _focusedNode.getAndUpdate {
            it?.onFocusStateChanged(false)
            null
        }
    }

    fun requestBlur(node: LayoutNode) {
        _focusedNode.getAndUpdate {
            if (it == node) {
                node.onFocusStateChanged(false)
                null
            } else {
                it
            }
        }
    }
}