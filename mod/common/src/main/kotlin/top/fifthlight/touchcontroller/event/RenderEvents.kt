package top.fifthlight.touchcontroller.event

import kotlinx.collections.immutable.toPersistentMap
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.paint.Canvas
import top.fifthlight.data.IntOffset
import top.fifthlight.data.Offset
import top.fifthlight.touchcontroller.config.LayoutLayerConditionKey
import top.fifthlight.touchcontroller.config.TouchControllerConfigHolder
import top.fifthlight.touchcontroller.gal.GameAction
import top.fifthlight.touchcontroller.gal.PlayerHandleFactory
import top.fifthlight.touchcontroller.gal.RidingEntityType
import top.fifthlight.touchcontroller.gal.WindowHandleFactory
import top.fifthlight.touchcontroller.layout.Context
import top.fifthlight.touchcontroller.layout.DrawQueue
import top.fifthlight.touchcontroller.layout.Hud
import top.fifthlight.touchcontroller.model.ControllerHudModel
import top.fifthlight.touchcontroller.model.GlobalStateModel
import top.fifthlight.touchcontroller.model.TouchStateModel
import top.fifthlight.touchcontroller.platform.PlatformHolder
import top.fifthlight.touchcontroller.proxy.message.AddPointerMessage
import top.fifthlight.touchcontroller.proxy.message.ClearPointerMessage
import top.fifthlight.touchcontroller.proxy.message.RemovePointerMessage
import top.fifthlight.touchcontroller.proxy.message.VibrateMessage

object RenderEvents : KoinComponent {
    private val windowHandle: WindowHandleFactory by inject()
    private val gameAction: GameAction by inject()
    private val configHolder: TouchControllerConfigHolder by inject()
    private val controllerHudModel: ControllerHudModel by inject()
    private val touchStateModel: TouchStateModel by inject()
    private val playerHandleFactory: PlayerHandleFactory by inject()
    private val globalStateModel: GlobalStateModel by inject()
    private val platformHolder: PlatformHolder by inject()

    fun onRenderStart() {
        val window = windowHandle.currentWindow

        globalStateModel.update()

        if (controllerHudModel.status.vibrate) {
            platformHolder.platform?.sendEvent(VibrateMessage(VibrateMessage.Kind.BLOCK_BROKEN))
            controllerHudModel.status.vibrate = false
        }

        platformHolder.platform?.let { platform ->
            while (true) {
                val message = platform.pollEvent() ?: break
                when (message) {
                    is AddPointerMessage -> {
                        touchStateModel.addPointer(
                            index = message.index,
                            position = Offset(
                                x = message.x,
                                y = message.y,
                            )
                        )
                    }

                    is RemovePointerMessage -> {
                        touchStateModel.removePointer(message.index)
                    }

                    ClearPointerMessage -> touchStateModel.clearPointer()

                    else -> {}
                }
            }
        }

        val config = configHolder.config.value
        if (config.enableTouchEmulation) {
            val mousePosition = window.mousePosition
            if (window.mouseLeftPressed && mousePosition != null) {
                touchStateModel.addPointer(
                    index = 0,
                    position = mousePosition / window.size.toSize()
                )
            } else {
                touchStateModel.clearPointer()
            }
        }

        val player = playerHandleFactory.getPlayerHandle() ?: return
        if (player.isFlying || player.isSubmergedInWater) {
            controllerHudModel.status.sneakLocked = false
        }
        val ridingType = player.ridingEntityType
        val condition = buildMap {
            put(LayoutLayerConditionKey.FLYING, player.isFlying)
            put(LayoutLayerConditionKey.SWIMMING, player.isSubmergedInWater)
            put(LayoutLayerConditionKey.SPRINTING, player.isSprinting)
            put(LayoutLayerConditionKey.SNEAKING, player.isSneaking)
            put(LayoutLayerConditionKey.ON_GROUND, player.onGround)
            put(LayoutLayerConditionKey.NOT_ON_GROUND, !player.onGround)
            put(LayoutLayerConditionKey.USING_ITEM, player.isUsingItem)
            put(LayoutLayerConditionKey.RIDING, ridingType != null)
            put(LayoutLayerConditionKey.ON_MINECART, ridingType == RidingEntityType.MINECART)
            put(LayoutLayerConditionKey.ON_BOAT, ridingType == RidingEntityType.BOAT)
            put(LayoutLayerConditionKey.ON_PIG, ridingType == RidingEntityType.PIG)
            put(LayoutLayerConditionKey.ON_HORSE, ridingType == RidingEntityType.HORSE)
            put(LayoutLayerConditionKey.ON_DONKEY, ridingType == RidingEntityType.DONKEY)
            put(LayoutLayerConditionKey.ON_LLAMA, ridingType == RidingEntityType.LLAMA)
            put(LayoutLayerConditionKey.ON_STRIDER, ridingType == RidingEntityType.STRIDER)
        }.toPersistentMap()

        val drawQueue = DrawQueue()
        val result = Context(
            windowSize = window.size,
            windowScaledSize = window.scaledSize,
            drawQueue = drawQueue,
            size = window.scaledSize,
            screenOffset = IntOffset.ZERO,
            pointers = touchStateModel.pointers,
            status = controllerHudModel.status,
            timer = controllerHudModel.timer,
            config = configHolder.config.value,
            condition = condition,
        ).run {
            Hud(
                layers = configHolder.layout.value,
            )
            result
        }
        controllerHudModel.result = result
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
            player.isFlying = false
        }
        if (result.chat) {
            gameAction.openChatScreen()
        }
        if (result.pause) {
            gameAction.openGameMenu()
        }
        result.lookDirection?.let { (x, y) ->
            player.changeLookDirection(x.toDouble(), y.toDouble())
        }
        result.inventory.slots.forEachIndexed { index, slot ->
            if (slot.select) {
                player.currentSelectedSlot = index
            }
            if (slot.drop) {
                val stack = player.getInventorySlot(index)
                if (stack.isEmpty) {
                    player.currentSelectedSlot = index
                } else {
                    player.dropSlot(index)
                }
            }
        }
    }

    fun onHudRender(canvas: Canvas) {
        val queue = controllerHudModel.pendingDrawQueue
        queue?.let {
            queue.execute(canvas)
            controllerHudModel.pendingDrawQueue = null
        }
    }

    fun shouldRenderCrosshair(): Boolean {
        val config = configHolder.config.value
        if (!config.disableCrosshair) {
            return true
        }
        val player = playerHandleFactory.getPlayerHandle() ?: return false
        return player.hasItemsOnHand(config.showCrosshairItems)
    }
}