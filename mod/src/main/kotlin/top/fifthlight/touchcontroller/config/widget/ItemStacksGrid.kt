package top.fifthlight.touchcontroller.config.widget

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import top.fifthlight.touchcontroller.ext.ceilDiv
import kotlin.math.abs

class ItemStacksGrid(
    private val client: MinecraftClient,
    x: Int = 0,
    y: Int = 0,
    width: Int = 0,
    height: Int = 0,
    message: Text,
    itemStacks: PersistentList<ItemStack> = persistentListOf(),
    private val onStackClicked: (Int, ItemStack) -> Unit = { _, _ -> }
) : ClickableWidget(x, y, width, height, message) {
    companion object {
        const val ITEM_SIZE = 18
        private const val ITEM_DISPLAY_SIZE = 16
        private const val ITEM_PADDING = (ITEM_SIZE - ITEM_DISPLAY_SIZE) / 2
    }

    private var scrollRows = 0
        set(value) {
            field = value.coerceIn(0..maxRows)
        }

    private val maxRows: Int
        get() {
            val columns = width / ITEM_SIZE
            val rows = height / ITEM_SIZE
            val totalRows = itemStacks.size ceilDiv columns
            return totalRows - rows
        }

    var itemStacks: PersistentList<ItemStack> = itemStacks
        set(value) {
            field = value
            // Trigger maxRows limit
            scrollRows = scrollRows
        }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val rows = height / ITEM_SIZE
        val columns = width / ITEM_SIZE

        val mouseRow = (mouseY - y) / ITEM_SIZE
        val mouseColumn = (mouseX - x) / ITEM_SIZE
        if (mouseRow in 0 until rows && mouseColumn in 0 until columns) {
            val mouseIndex = (mouseRow + scrollRows) * columns + mouseColumn
            val mouseStackX = x + mouseColumn * ITEM_SIZE
            val mouseStackY = y + mouseRow * ITEM_SIZE
            itemStacks.getOrNull(mouseIndex)?.let {
                context.fill(
                    mouseStackX,
                    mouseStackY,
                    mouseStackX + ITEM_SIZE,
                    mouseStackY + ITEM_SIZE,
                    0x80FFFFFFU.toInt()
                )
                context.drawItemTooltip(client.textRenderer, it, mouseX, mouseY)
            }
        }

        for (row in 0 until rows) {
            for (column in 0 until columns) {
                val index = (row + scrollRows) * columns + column
                val itemStack = itemStacks.getOrNull(index) ?: continue
                context.drawItemWithoutEntity(
                    itemStack,
                    x + column * ITEM_SIZE + ITEM_PADDING,
                    y + row * ITEM_SIZE + ITEM_PADDING
                )
            }
        }
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        when {
            verticalAmount > 0 -> {
                scrollRows -= 1
            }

            verticalAmount < 0 -> {
                scrollRows += 1
            }
        }
        return true
    }

    private var mouseDownPositionX = 0.0
    private var mouseDownPositionY = 0.0
    private var mouseDownScrollRows = scrollRows

    override fun playDownSound(soundManager: SoundManager) {}

    override fun onClick(mouseX: Double, mouseY: Double) {
        mouseDownPositionX = mouseX
        mouseDownPositionY = mouseY
        mouseDownScrollRows = scrollRows
    }

    override fun onRelease(mouseX: Double, mouseY: Double) {
        val diffX = mouseX - mouseDownPositionX
        val diffY = mouseY - mouseDownPositionY
        if ((abs(diffX) >= ITEM_SIZE && abs(diffY) >= ITEM_SIZE) || scrollRows != mouseDownScrollRows) {
            return
        }

        val columns = width / ITEM_SIZE
        val mouseRow = (mouseY.toInt() - y) / ITEM_SIZE
        val mouseColumn = (mouseX.toInt() - x) / ITEM_SIZE
        val mouseIndex = (mouseRow + scrollRows) * columns + mouseColumn
        val mouseStack = itemStacks.getOrNull(mouseIndex) ?: return
        onStackClicked(mouseIndex, mouseStack)
        playClickSound(client.soundManager)
    }

    override fun onDrag(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double) {
        val diffY = mouseDownPositionY - mouseY
        scrollRows = mouseDownScrollRows + (diffY / ITEM_SIZE).toInt()
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder) {}
}