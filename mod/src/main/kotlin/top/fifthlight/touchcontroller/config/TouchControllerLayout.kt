package top.fifthlight.touchcontroller.config

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import top.fifthlight.data.IntOffset
import top.fifthlight.touchcontroller.control.*
import top.fifthlight.touchcontroller.layout.Align

@Serializable
data class LayoutLayerCondition(
    val swimming: Boolean = true,
    val flying: Boolean = true,
) {
    fun check(other: LayoutLayerCondition): Boolean {
        if (this.swimming && other.swimming) {
            return true
        }
        if (this.flying && other.flying) {
            return true
        }
        return false
    }
}

@Serializable
data class LayoutLayer(
    val widgets: PersistentList<ControllerWidget> = persistentListOf(),
    val condition: LayoutLayerCondition = LayoutLayerCondition(),
)

typealias TouchControllerLayout = PersistentList<LayoutLayer>

val defaultTouchControllerLayout: TouchControllerLayout = persistentListOf(
    LayoutLayer(
        widgets = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(8, 8),
                opacity = 0.6f
            ),
            JumpButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 68),
                opacity = 0.6f
            ),
            AscendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 116),
                opacity = 0.6f
            ),
            DescendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 20),
                opacity = 0.6f
            ),
            PauseButton(
                align = Align.CENTER_TOP,
                offset = IntOffset(-9, 0),
                opacity = 0.6f
            ),
            ChatButton(
                align = Align.CENTER_TOP,
                offset = IntOffset(9, 0),
                opacity = 0.6f
            ),
            InventoryButton()
        )
    )
)