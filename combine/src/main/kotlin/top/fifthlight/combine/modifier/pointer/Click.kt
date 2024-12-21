package top.fifthlight.combine.modifier.pointer

import top.fifthlight.combine.modifier.Modifier

fun Modifier.clickable(onClick: () -> Unit) = then(ClickableModifierNode(onClick))

private data class ClickableModifierNode(
    val onClick: () -> Unit
) : Modifier.Node<ClickableModifierNode>
