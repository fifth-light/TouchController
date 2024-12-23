package top.fifthlight.combine.input

fun interface PointerEventReceiver {
    fun onPointerEvent(event: PointerEvent): Boolean
}
