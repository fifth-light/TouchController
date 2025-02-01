package top.fifthlight.touchcontroller.layout

import org.koin.core.component.get
import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.gal.GameFeatures
import top.fifthlight.touchcontroller.gal.KeyBindingType
import top.fifthlight.touchcontroller.gal.PlayerHandleFactory
import top.fifthlight.touchcontroller.state.PointerState

private const val INVENTORY_SLOT_HOLD_DROP_TIME = 40

private fun Context.InventorySlot(index: Int) {
    val gameFeatures: GameFeatures = get()
    val playerHandleFactory: PlayerHandleFactory = get()
    val player = playerHandleFactory.getPlayerHandle() ?: return

    val pointers = getPointersInRect(size)
    val slot = result.inventory.slots[index]
    for (pointer in pointers) {
        when (val state = pointer.state) {
            PointerState.New -> {
                pointer.state = PointerState.InventorySlot(index, timer.tick)
            }

            is PointerState.InventorySlot -> {
                if (state.index == index) {
                    val time = timer.tick - state.startTick
                    slot.progress = time.toFloat() / INVENTORY_SLOT_HOLD_DROP_TIME
                    if (time == INVENTORY_SLOT_HOLD_DROP_TIME) {
                        slot.drop = true
                        pointer.state = PointerState.Invalid
                    }
                }
            }

            is PointerState.Released -> {
                val previousState = state.previousState
                if (previousState is PointerState.InventorySlot && previousState.index == index) {
                    slot.select = true
                    if (gameFeatures.dualWield || config.quickHandSwap) {
                        if (player.currentSelectedSlot == index) {
                            if (status.quickHandSwap.click(timer.tick)) {
                                keyBindingHandler.getState(KeyBindingType.SWAP_HANDS).clicked = true
                            }
                        }
                    } else {
                        status.quickHandSwap.clear()
                    }
                }
            }

            else -> {}
        }
    }
}

fun Context.Inventory() {
    withAlign(align = Align.CENTER_BOTTOM, size = IntSize(182, 22)) {
        withRect(x = 1, y = 1, width = 180, height = 20) {
            for (i in 0 until 9) {
                withRect(x = 20 * i, y = 0, width = 20, height = 20) {
                    InventorySlot(i)
                }
            }
        }
    }
}
