package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import top.fifthlight.data.IntSize
import top.fifthlight.data.Offset

object WindowHandleImpl : WindowHandle {
    private val client = Minecraft.getMinecraft()

    override val size: IntSize
        get() = IntSize(
            width = client.displayWidth,
            height = client.displayHeight
        )

    override val scaledSize: IntSize
        get() {
            val resolution = ScaledResolution(client)
            return IntSize(
                width = resolution.scaledWidth,
                height = resolution.scaledHeight,
            )
        }

    override val mouseLeftPressed: Boolean
        get() = false // TODO client.mouseHandler.isLeftPressed

    override val mousePosition: Offset
        get() = Offset.ZERO/* TODO
        Offset(
            x = client.mouseHandler.xpos().toFloat(),
            y = client.mouseHandler.ypos().toFloat(),
        )*/
}