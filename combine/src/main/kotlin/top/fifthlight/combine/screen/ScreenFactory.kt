package top.fifthlight.combine.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import top.fifthlight.combine.data.Text

val LocalScreenFactory = staticCompositionLocalOf<ScreenFactory> { error("No ScreenFactory in context") }

interface ScreenFactory {
    fun <M : ViewModel> openScreen(
        title: Text,
        viewModelFactory: (CoroutineScope) -> M,
        content: @Composable (M) -> Unit,
    )

    fun <M : ViewModel> getScreen(
        parent: Any?,
        title: Text,
        viewModelFactory: (CoroutineScope) -> M,
        content: @Composable (M) -> Unit,
    ): Any
}