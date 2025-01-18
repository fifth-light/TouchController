@file:Suppress("unused")

package top.fifthlight.touchcontroller.helper

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiIngame
import net.minecraft.client.gui.ScaledResolution
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.touchcontroller.event.RenderEvents
import top.fifthlight.touchcontroller.model.ControllerHudModel

object GuiIngameHelper : KoinComponent {
    private val controllerHudModel: ControllerHudModel by inject()
    private val client = Minecraft.getMinecraft()

    @JvmStatic
    fun renderHotbar(scaledResolution: ScaledResolution) {
        val player = client.player ?: return
        var inventory = controllerHudModel.result.inventory
        var x = (scaledResolution.scaledWidth - 182) / 2 + 1
        var y = scaledResolution.scaledHeight - 22 + 1
        for (i in 0 until 9) {
            var stack = player.inventory.getStackInSlot(i)
            if (stack.isEmpty) {
                continue
            }
            var slot = inventory.slots[i]
            var height = (16 * slot.progress).toInt()
            Gui.drawRect(x + 20 * i + 2, y + 18 - height, x + 20 * i + 18, y + 18, 0xFF00BB00U.toInt())
        }
    }

    @JvmStatic
    fun drawTexturedModalRectWrapper(
        guiInGame: GuiIngame,
        x: Int,
        y: Int,
        textureX: Int,
        textureY: Int,
        width: Int,
        height: Int,
        times: Int
    ) {
        when (times) {
            0 -> {
                // crosshair
                if (RenderEvents.shouldRenderCrosshair()) {
                    guiInGame.drawTexturedModalRect(x, y, textureX, textureY, width, height)
                }
            }

            1, 2, 3 -> {
                // attack indicator
                if (RenderEvents.shouldRenderCrosshair()) {
                    guiInGame.drawTexturedModalRect(x, y, textureX, textureY, width, height)
                } else {
                    guiInGame.drawTexturedModalRect(x, y - 8, textureX, textureY, width, height)
                }
            }

            else -> {
                guiInGame.drawTexturedModalRect(x, y, textureX, textureY, width, height)
            }
        }
    }
}