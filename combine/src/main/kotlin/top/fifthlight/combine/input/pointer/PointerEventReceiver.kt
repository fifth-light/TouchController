package top.fifthlight.combine.input.pointer

fun interface PointerEventReceiver {
    fun onPointerEvent(event: PointerEvent): Boolean
}
