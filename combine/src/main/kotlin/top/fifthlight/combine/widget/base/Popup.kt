package top.fifthlight.combine.widget.base

import androidx.compose.runtime.*
import top.fifthlight.combine.node.LocalCombineOwner

@Composable
fun Popup(
    onDismissRequest: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val owner = LocalCombineOwner.current
    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)
    val layer = remember {
        owner.addLayer(
            parentContext = parentComposition,
            onDismissRequest = onDismissRequest,
            content = currentContent,
        )
    }
    DisposableEffect(layer) {
        onDispose {
            layer.dispose()
        }
    }
}
