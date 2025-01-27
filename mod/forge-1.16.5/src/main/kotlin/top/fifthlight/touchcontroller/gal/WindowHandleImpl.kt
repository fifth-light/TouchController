package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft
import top.fifthlight.data.IntSize
import top.fifthlight.data.Offset

object WindowHandleImpl : WindowHandle {
    private val client = Minecraft.getInstance()
    private val window by lazy { client.window }

    override val size: IntSize
        get() = IntSize(
            width = window.width,
            height = window.height
        )

    override val scaledSize: IntSize
        get() = IntSize(
            width = window.guiScaledWidth,
            height = window.guiScaledHeight
        )

    override val mouseLeftPressed: Boolean
        get() = client.mouseHandler.isLeftPressed

    override val mousePosition: Offset
        get() = Offset(
            x = client.mouseHandler.xpos().toFloat(),
            y = client.mouseHandler.ypos().toFloat(),
        )
}