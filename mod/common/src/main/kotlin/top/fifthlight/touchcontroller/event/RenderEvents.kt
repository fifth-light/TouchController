package top.fifthlight.touchcontroller.event

import kotlinx.collections.immutable.toPersistentMap
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.paint.Canvas
import top.fifthlight.data.IntOffset
import top.fifthlight.data.Offset
import top.fifthlight.touchcontroller.config.GlobalConfigHolder
import top.fifthlight.touchcontroller.config.LayerConditionKey
import top.fifthlight.touchcontroller.gal.*
import top.fifthlight.touchcontroller.layout.Context
import top.fifthlight.touchcontroller.layout.DrawQueue
import top.fifthlight.touchcontroller.layout.Hud
import top.fifthlight.touchcontroller.model.ControllerHudModel
import top.fifthlight.touchcontroller.model.TouchStateModel
import top.fifthlight.touchcontroller.platform.PlatformHolder
import top.fifthlight.touchcontroller.proxy.message.AddPointerMessage
import top.fifthlight.touchcontroller.proxy.message.ClearPointerMessage
import top.fifthlight.touchcontroller.proxy.message.RemovePointerMessage
import top.fifthlight.touchcontroller.proxy.message.VibrateMessage

object RenderEvents : KoinComponent {
    private val window: WindowHandle by inject()
    private val gameAction: GameAction by inject()
    private val configHolder: GlobalConfigHolder by inject()
    private val controllerHudModel: ControllerHudModel by inject()
    private val touchStateModel: TouchStateModel by inject()
    private val playerHandleFactory: PlayerHandleFactory by inject()
    private val platformHolder: PlatformHolder by inject()
    private val gameStateProvider: GameStateProvider by inject()

    fun onRenderStart() {
        if (controllerHudModel.status.vibrate) {
            platformHolder.platform?.sendEvent(VibrateMessage(VibrateMessage.Kind.BLOCK_BROKEN))
            controllerHudModel.status.vibrate = false
        }

        val gameState = gameStateProvider.currentState()
        if (gameState.inGame && !gameState.inGui) {
            val platform = platformHolder.platform
            if (platform != null) {
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
        } else {
            touchStateModel.clearPointer()
        }

        val player = playerHandleFactory.getPlayerHandle() ?: return
        if (player.isFlying || player.isSubmergedInWater) {
            controllerHudModel.status.sneakLocked = false
        }



        val ridingType = player.ridingEntityType
        val condition = buildMap {
            put(LayerConditionKey.FLYING, player.isFlying)
            put(LayerConditionKey.SWIMMING, player.isTouchingWater)
            put(LayerConditionKey.UNDERWATER, player.isSubmergedInWater)
            put(LayerConditionKey.SPRINTING, player.isSprinting)
            put(LayerConditionKey.SNEAKING, player.isSneaking)
            put(LayerConditionKey.ON_GROUND, player.onGround)
            put(LayerConditionKey.NOT_ON_GROUND, !player.onGround)
            put(LayerConditionKey.USING_ITEM, player.isUsingItem)
            put(LayerConditionKey.RIDING, ridingType != null)
            put(LayerConditionKey.ON_MINECART, ridingType == RidingEntityType.MINECART)
            put(LayerConditionKey.ON_BOAT, ridingType == RidingEntityType.BOAT)
            put(LayerConditionKey.ON_PIG, ridingType == RidingEntityType.PIG)
            put(LayerConditionKey.ON_HORSE, ridingType == RidingEntityType.HORSE)
            put(LayerConditionKey.ON_CAMEL, ridingType == RidingEntityType.CAMEL)
            put(LayerConditionKey.ON_LLAMA, ridingType == RidingEntityType.LLAMA)
            put(LayerConditionKey.ON_STRIDER, ridingType == RidingEntityType.STRIDER)
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
                layers = configHolder.layout.value.layers,
            )
            result
        }
        controllerHudModel.result = result
        controllerHudModel.pendingDrawQueue = drawQueue

        val status = controllerHudModel.status
        if (result.sprint || status.sprintLocked) {
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