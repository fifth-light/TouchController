package top.fifthlight.combine.modifier.pointer

import top.fifthlight.combine.input.PointerInputScope
import top.fifthlight.combine.modifier.Modifier

fun Modifier.pointerInput(block: suspend PointerInputScope.() -> Unit) = then(TODO())