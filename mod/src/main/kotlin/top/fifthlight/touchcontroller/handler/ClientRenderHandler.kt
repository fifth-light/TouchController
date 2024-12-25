package top.fifthlight.touchcontroller.handler

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.data.IntOffset
import top.fifthlight.touchcontroller.config.TouchControllerConfigHolder
import top.fifthlight.touchcontroller.event.ClientRenderEvents
import top.fifthlight.touchcontroller.ext.scaledSize
import top.fifthlight.touchcontroller.layout.Context
import top.fifthlight.touchcontroller.layout.DrawQueue
import top.fifthlight.touchcontroller.layout.Hud
import top.fifthlight.touchcontroller.layout.HudState
import top.fifthlight.touchcontroller.mixin.ClientOpenChatScreenInvoker
import top.fifthlight.touchcontroller.mixin.ClientPlayerInteractionManagerMixin
import top.fifthlight.touchcontroller.model.ControllerHudModel
import top.fifthlight.touchcontroller.model.TouchStateModel

class ClientRenderHandler : ClientRenderEvents.StartRenderTick, KoinComponent {
    private val configHolder: TouchControllerConfigHolder by inject()
    private val controllerHudModel: ControllerHudModel by inject()
    private val touchStateModel: TouchStateModel by inject()

    private fun Entity.changeLookDirectionByDegrees(deltaYaw: Double, deltaPitch: Double) {
        // Magic value 0.15 from net.minecraft.entity.Entity.changeLookDirection
        changeLookDirection(deltaYaw / 0.15, deltaPitch / 0.15)
    }

    override fun onStartTick(client: MinecraftClient, tick: Boolean) {
        val player = client.player ?: return
        val state = if (player.isSubmergedInWater) {
            HudState.SWIMMING
        } else if (player.abilities.flying) {
            HudState.FLYING
        } else {
            HudState.NORMAL
        }
        if (state != HudState.NORMAL) controllerHudModel.status.sneakLocked = false
        val drawQueue = DrawQueue()
        val result = Context(
            drawQueue = drawQueue,
            size = client.window.scaledSize,
            screenOffset = IntOffset.ZERO,
            scale = client.window.scaleFactor.toFloat(),
            pointers = touchStateModel.pointers,
            status = controllerHudModel.status,
            timer = controllerHudModel.timer,
            state = state,
            config = configHolder.config.value
        ).run {
            Hud(
                widgets = configHolder.layout.value,
            )
            result
        }
        controllerHudModel.result = result
        if (state != HudState.NORMAL) controllerHudModel.status.sneakLocked = false
        controllerHudModel.pendingDrawQueue = drawQueue

        val status = controllerHudModel.status
        if (result.sprint) {
            status.wasSprinting = true
        } else {
            if (status.wasSprinting) {
                status.wasSprinting = false
                player.isSprinting = false
            }
        }
        if (result.cancelFlying) {
            player.abilities?.flying = false
        }
        if (result.chat) {
            (client as ClientOpenChatScreenInvoker).callOpenChatScreen("")
        }
        if (result.pause) {
            client.openGameMenu(false)
        }
        result.lookDirection?.let { (x, y) ->
            player.changeLookDirectionByDegrees(x.toDouble(), y.toDouble())
        }
        result.inventory.slots.forEachIndexed { index, slot ->
            if (slot.select) {
                player.inventory.setSelectedSlot(index)
            }
            if (slot.drop) {
                val stack = player.inventory.getStack(index)
                if (stack.isEmpty) {
                    player.inventory.setSelectedSlot(index)
                } else {
                    val originalSlot = player.inventory.selectedSlot
                    val interactionManagerAccessor = client.interactionManager as ClientPlayerInteractionManagerMixin

                    player.inventory.setSelectedSlot(index)
                    interactionManagerAccessor.callSyncSelectedSlot()

                    player.dropSelectedItem(true)

                    player.inventory.setSelectedSlot(originalSlot)
                    interactionManagerAccessor.callSyncSelectedSlot()
                }
            }
        }
    }
}