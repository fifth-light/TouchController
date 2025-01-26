package top.fifthlight.touchcontroller.gal

import net.minecraft.client.MinecraftClient
import top.fifthlight.data.IntSize
import top.fifthlight.data.Offset

object WindowHandleImpl : WindowHandle {
    private val client = MinecraftClient.getInstance()
    private val window by lazy { client.window }

    override val size: IntSize
        get() = IntSize(
            width = window.width,
            height = window.height
        )

    override val scaledSize: IntSize
        get() = IntSize(
            width = window.scaledWidth,
            height = window.scaledHeight
        )

    override val mouseLeftPressed: Boolean
        get() = client.mouse.wasLeftButtonClicked()

    override val mousePosition: Offset
        get() = Offset(
            x = client.mouse.x.toFloat(),
            y = client.mouse.y.toFloat(),
        )
}