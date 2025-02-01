package top.fifthlight.touchcontroller.layout

import org.koin.core.component.get
import top.fifthlight.data.Offset
import top.fifthlight.touchcontroller.gal.CrosshairTarget
import top.fifthlight.touchcontroller.gal.KeyBindingType
import top.fifthlight.touchcontroller.gal.PlayerHandleFactory
import top.fifthlight.touchcontroller.gal.ViewActionProvider
import top.fifthlight.touchcontroller.state.PointerState
import top.fifthlight.touchcontroller.state.PointerState.View.ViewPointerState.*

fun Context.View() {
    val viewActionProvider: ViewActionProvider = get()

    fun Offset.fixAspectRadio(): Offset = Offset(
        x = x,
        y = y * windowSize.height / windowSize.width
    )

    val attackKeyState = keyBindingHandler.getState(KeyBindingType.ATTACK)
    val useKeyState = keyBindingHandler.getState(KeyBindingType.USE)

    var releasedView = false
    for (key in pointers.keys.toList()) {
        val state = pointers[key]!!.state
        if (state is PointerState.Released) {
            if (state.previousState is PointerState.View) {
                val previousState = state.previousState
                when (previousState.viewState) {
                    CONSUMED -> {}
                    BREAKING -> {
                        attackKeyState.locked = false
                    }

                    USING -> {
                        useKeyState.locked = false
                    }

                    INITIAL -> {
                        if (!releasedView) {
                            val pressTime = timer.tick - previousState.pressTime
                            // Pressed less than 5 ticks and not moving, recognized as short click
                            if (pressTime < 5 && !previousState.moving) {
                                val crosshairTarget = viewActionProvider.getCrosshairTarget() ?: break
                                when (crosshairTarget) {
                                    CrosshairTarget.BLOCK -> {
                                        // Short click on block: use item
                                        useKeyState.click()
                                    }

                                    CrosshairTarget.ENTITY -> {
                                        // Short click on entity: attack the entity
                                        attackKeyState.click()
                                    }

                                    CrosshairTarget.MISS -> {}
                                }
                            }
                        }
                    }
                }
                releasedView = true
            }
            // Remove all released pointers, because View is the last layout
            pointers.remove(key)
        }
    }

    var currentViewPointer = pointers.values.firstOrNull {
        it.state is PointerState.View
    }

    currentViewPointer?.let { pointer ->
        val state = pointer.state as PointerState.View

        // Drop all unhandled pointers
        pointers.values.forEach {
            when (it.state) {
                PointerState.New -> it.state = PointerState.Invalid
                else -> {}
            }
        }

        var moving = state.moving
        if (!state.moving) {
            // Move detect
            val delta = (pointer.position - state.initialPosition).fixAspectRadio().squaredLength
            val threshold = config.viewHoldDetectThreshold * 0.01f
            if (delta > threshold * threshold) {
                moving = true
            }
        }

        val movement = (pointer.position - state.lastPosition).fixAspectRadio()
        result.lookDirection = movement * config.viewMovementSensitivity

        val playerHandleFactory: PlayerHandleFactory = get()
        val player = playerHandleFactory.getPlayerHandle()
        // Consume the pointer if player is null or touch gesture is disabled
        if (player == null || config.disableTouchGesture) {
            pointer.state = state.copy(
                lastPosition = pointer.position,
                moving = moving,
                viewState = CONSUMED
            )
            return@let
        }

        // Early exit for consumed pointer
        if (state.viewState == CONSUMED) {
            pointer.state = state.copy(lastPosition = pointer.position, moving = moving)
            return@let
        }

        val pressTime = timer.tick - state.pressTime
        var viewState = state.viewState
        val crosshairTarget = viewActionProvider.getCrosshairTarget()
        val itemUsable = player.hasItemsOnHand(config.usableItems)

        // If pointer kept still and held for 5 tick
        if (pressTime == 5 && !moving) {
            if (itemUsable) {
                // Trigger item long click
                useKeyState.locked = true
                viewState = USING
            } else {
                when (crosshairTarget) {
                    CrosshairTarget.BLOCK -> {
                        // Trigger block breaking
                        attackKeyState.locked = true
                        viewState = BREAKING
                    }

                    CrosshairTarget.ENTITY -> {
                        // Trigger item use once and consume
                        useKeyState.click()
                        viewState = CONSUMED
                    }

                    CrosshairTarget.MISS, null -> {}
                }
            }
        }

        pointer.state = state.copy(
            lastPosition = pointer.position,
            moving = moving,
            viewState = viewState
        )
    } ?: run {
        pointers.values.forEach {
            when (it.state) {
                PointerState.New -> {
                    if (currentViewPointer != null) {
                        it.state = PointerState.Invalid
                    } else {
                        it.state = PointerState.View(
                            initialPosition = it.position,
                            lastPosition = it.position,
                            pressTime = timer.tick,
                            viewState = INITIAL
                        )
                        currentViewPointer = it
                    }
                }

                else -> {}
            }
        }
    }

    currentViewPointer?.let { pointer ->
        result.showBlockOutline = true
        // Update current view pointer
        if (!config.splitControls) {
            result.crosshairStatus = CrosshairStatus(
                position = pointer.position,
                breakPercent = viewActionProvider.getCurrentBreakingProgress(),
            )
        }
    } ?: run {
        if (attackKeyState.haveClickCount() || useKeyState.haveClickCount()) {
            // Keep last crosshair status for key handling
            result.crosshairStatus = status.lastCrosshairStatus
        }
    }

    if (config.disableTouchGesture) {
        result.showBlockOutline = true
    }

    status.lastCrosshairStatus = result.crosshairStatus
}