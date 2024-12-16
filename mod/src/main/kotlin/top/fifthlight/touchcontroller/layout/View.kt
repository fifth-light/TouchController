package top.fifthlight.touchcontroller.layout

import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Item
import net.minecraft.item.ProjectileItem
import net.minecraft.item.RangedWeaponItem
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult.Type.*
import top.fifthlight.touchcontroller.config.TouchControllerConfig
import top.fifthlight.touchcontroller.mixin.ClientPlayerInteractionManagerMixin
import top.fifthlight.touchcontroller.proxy.data.Offset
import top.fifthlight.touchcontroller.state.PointerState
import top.fifthlight.touchcontroller.state.PointerState.View.ViewPointerState.*

private fun Item.isUsable(config: TouchControllerConfig): Boolean {
    if (this in config.usableItems.items) {
        return true
    } else if (config.foodUsable && components.get(DataComponentTypes.FOOD) != null) {
        return true
    } else if (config.projectileUsable && this is ProjectileItem) {
        return true
    } else if (config.rangedWeaponUsable && this is RangedWeaponItem) {
        return true
    } else if (config.equippableUsable && components.get(DataComponentTypes.EQUIPPABLE) != null) {
        return true
    } else if (config.bundleUsable && components.get(DataComponentTypes.BUNDLE_CONTENTS) != null) {
        return true
    } else {
        return false
    }
}

fun Context.View() {
    fun Offset.fixAspectRadio(): Offset = Offset(
        x = x,
        y = y * window.height / window.width
    )

    var releasedView = false
    for (key in pointers.keys.toList()) {
        val state = pointers[key]!!.state
        if (state is PointerState.Released) {
            if (state.previousState is PointerState.View) {
                val previousState = state.previousState
                when (previousState.viewState) {
                    CONSUMED -> {}
                    BREAKING -> {
                        status.attack.release()
                    }

                    USING -> {
                        status.itemUse.release()
                    }

                    INITIAL -> {
                        if (!releasedView) {
                            val pressTime = timer.tick - previousState.pressTime
                            // Pressed less than 5 ticks and not moving, recognized as short click
                            if (pressTime < 5 && !previousState.moving) {
                                val crosshairTarget = client.crosshairTarget ?: break
                                when (crosshairTarget.type) {
                                    BLOCK -> {
                                        // Short click on block: use item
                                        status.itemUse.click()
                                    }

                                    ENTITY -> {
                                        // Short click on entity: attack the entity
                                        status.attack.click()
                                    }

                                    MISS, null -> {}
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

        val player = client.player
        // Consume the pointer if player is null
        if (player == null) {
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
        val crosshairTarget = client.crosshairTarget
        val itemUsable = Hand.entries.any { hand ->
            player.getStackInHand(hand).item.isUsable(config)
        }

        // If pointer kept still and held for 5 tick
        if (pressTime == 5 && !moving) {
            if (itemUsable) {
                // Trigger item long click
                status.itemUse.press()
                viewState = USING
            } else {
                when (crosshairTarget?.type) {
                    BLOCK -> {
                        // Trigger block breaking
                        status.attack.press()
                        viewState = BREAKING
                    }

                    ENTITY -> {
                        // Trigger item use once and consume
                        status.itemUse.click()
                        viewState = CONSUMED
                    }

                    MISS, null -> {}
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
        // Update current view pointer
        val manager = client.interactionManager
        val accessor = manager as ClientPlayerInteractionManagerMixin

        result.crosshairStatus = CrosshairStatus(
            position = pointer.position,
            breakPercent = accessor.currentBreakingProgress,
        )
    } ?: run {
        if (status.attack.timesPressed > 0 || status.itemUse.timesPressed > 0) {
            // Keep last crosshair status for key handling
            result.crosshairStatus = status.lastCrosshairStatus
        }
    }

    status.lastCrosshairStatus = result.crosshairStatus
}