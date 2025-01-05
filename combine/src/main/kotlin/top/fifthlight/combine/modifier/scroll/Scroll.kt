package top.fifthlight.combine.modifier.scroll

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.fifthlight.data.Offset

class ScrollState {
    private val _progress = MutableStateFlow(0)
    val progress = _progress.asStateFlow()
    internal var contentHeight = 0
    internal var viewportHeight = 0
    internal var initialPointerPosition: Offset? = null
    internal var startProgress = 0
    internal var startPointerPosition: Offset? = null
    internal var scrolling: Boolean = false

    internal fun updateProgress(progress: Int) {
        val maxProgress = (contentHeight - viewportHeight).takeIf { it > 0 }

        _progress.value = maxProgress?.let {
            progress.coerceIn(0, maxProgress)
        } ?: run {
            0
        }
    }
}

@Composable
fun rememberScrollState() = remember { ScrollState() }