package top.fifthlight.touchcontroller.gal

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Mouse
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
        get() = Mouse.isButtonDown(0)

    override val mousePosition: Offset
        get() = Offset(
            x = Mouse.getX().toFloat(),
            y = Mouse.getY().toFloat(),
        )
}