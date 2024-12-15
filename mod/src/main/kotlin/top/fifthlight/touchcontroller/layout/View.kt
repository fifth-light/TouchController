package top.fifthlight.touchcontroller.layout

import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Item
import net.minecraft.item.ProjectileItem
import net.minecraft.item.RangedWeaponItem
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult.Type.*
import top.fifthlight.touchcontroller.config.TouchControllerConfig
import top.fifthlight.touchcontroller.ext.size
import top.fifthlight.touchcontroller.mixin.ClientPlayerInteractionManagerAccessor
import top.fifthlight.touchcontroller.state.PointerState

private fun Item.isUsable(config: TouchControllerConfig): Boolean {
    if (config.foodUsable && components.get(DataComponentTypes.FOOD) != null) {
        return true
    } else if (config.projectileUsable && this is ProjectileItem) {
        return true
    } else if (config.rangedWeaponUsable && this is RangedWeaponItem) {
        return true
    } else if (config.equippableUsable && components.get(DataComponentTypes.EQUIPPABLE) != null) {
        return true
    } else if (this in config.usableItems.items) {
        return true
    }
    return false
}

fun Context.View() {
    var releasedView = false
    for (key in pointers.keys.toList()) {
        val state = pointers[key]!!.state
        if (state is PointerState.Released) {
            if (!releasedView && state.previousState is PointerState.View) {
                releasedView = true
                val previousState = state.previousState
                val pressTime = timer.tick - previousState.pressTime
                if (pressTime < 5 && !previousState.moving) {
                    val crosshairTarget = client.crosshairTarget ?: break
                    when (crosshairTarget.type) {
                        BLOCK -> {
                            status.startItemUse = true
                            status.itemUse.add()
                        }

                        ENTITY -> {
                            status.attack.add()
                        }

                        MISS, null -> {}
                    }
                }
            }
            pointers.remove(key)
        }
    }

    var currentViewPointer = pointers.values.firstOrNull {
        it.state is PointerState.View
    }

    currentViewPointer?.let { pointer ->
        val state = pointer.state as PointerState.View
        pointers.values.forEach {
            when (it.state) {
                PointerState.New -> it.state = PointerState.Invalid
                else -> {}
            }
        }

        var moving = state.moving
        if (!state.moving) {
            val delta = (pointer.rawOffset - state.initialPosition).squaredLength
            val threshold = (client.window.size.toSize() * 0.02f).squaredLength
            if (delta > threshold) {
                moving = true
            }
        }
        result.lookDirection = pointer.rawOffset - state.lastPosition

        if (state.consumed) {
            pointer.state = state.copy(lastPosition = pointer.rawOffset, moving = moving)
            return@let
        }

        var consumed = false
        var itemUsable = false
        val player = client.player
        val pressTime = timer.tick - state.pressTime
        var longPressTriggered = state.longPressTriggered
        val crosshairTarget = client.crosshairTarget

        if (player != null) {
            for (hand in Hand.entries) {
                val stack = player.getStackInHand(hand)
                if (stack.item.isUsable(config)) {
                    itemUsable = true
                    break
                }
            }
        }

        if (pressTime == 5 && !moving) {
            if (itemUsable) {
                longPressTriggered = true
                status.startItemUse = true
            } else {
                when (crosshairTarget?.type) {
                    BLOCK -> {
                        longPressTriggered = true
                        status.startItemUse = true
                    }

                    ENTITY -> {
                        status.itemUse.add()
                        status.startItemUse = true
                        consumed = true
                    }

                    MISS, null -> {}
                }
            }
        }

        if (!consumed && longPressTriggered) {
            if (itemUsable) {
                status.itemUse.add()
            } else if (crosshairTarget?.type == BLOCK) {
                status.attack.add()
            }
        }

        pointer.state = state.copy(
            lastPosition = pointer.rawOffset,
            moving = moving,
            longPressTriggered = longPressTriggered,
            consumed = consumed
        )
    } ?: run {
        pointers.values.forEach {
            when (it.state) {
                PointerState.New -> {
                    if (currentViewPointer != null) {
                        it.state = PointerState.Invalid
                    } else {
                        it.state = PointerState.View(
                            initialPosition = it.rawOffset, lastPosition = it.rawOffset, pressTime = timer.tick
                        )
                        currentViewPointer = it
                    }
                }

                else -> {}
            }
        }
    }

    currentViewPointer?.let { pointer ->
        val manager = client.interactionManager
        val accessor = manager as ClientPlayerInteractionManagerAccessor

        result.crosshairStatus = CrosshairStatus(
            position = pointer.position,
            breakPercent = accessor.currentBreakingProgress,
        )
    } ?: run {
        if (status.attack.active() || status.itemUse.active()) {
            result.crosshairStatus = status.lastCrosshairStatus
        }
    }

    status.lastCrosshairStatus = result.crosshairStatus
}