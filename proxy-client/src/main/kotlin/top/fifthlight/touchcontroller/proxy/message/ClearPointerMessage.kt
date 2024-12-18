package top.fifthlight.touchcontroller.proxy.message

data object ClearPointerMessage : ProxyMessage() {
    override val type: Int = 3
}