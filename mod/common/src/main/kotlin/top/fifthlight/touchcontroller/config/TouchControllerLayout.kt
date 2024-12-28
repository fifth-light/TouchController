package top.fifthlight.touchcontroller.config

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable
import top.fifthlight.data.IntOffset
import top.fifthlight.touchcontroller.control.*
import top.fifthlight.touchcontroller.layout.Align

enum class LayoutLayerConditionValue {
    NEVER,
    REQUIRE,
}

enum class LayoutLayerConditionKey {
    SWIMMING,
    FLYING,
    SNEAKING,
    SPRINTING,
    ON_GROUND,
    NOT_ON_GROUND,
    USING_ITEM,
    ON_MINECART,
    ON_BOAT,
    ON_PIG,
    ON_HORSE,
    ON_DONKEY,
    ON_LLAMA,
    ON_STRIDER,
    RIDING,
}

@Serializable
@JvmInline
value class LayoutLayerCondition(
    val conditions: PersistentMap<LayoutLayerConditionKey, LayoutLayerConditionValue> = persistentMapOf()
) {
    fun check(currentState: PersistentMap<LayoutLayerConditionKey, Boolean>): Boolean {
        for (condition in conditions) {
            val current = currentState[condition.key]
            when (condition.value) {
                LayoutLayerConditionValue.NEVER -> if (current == true) {
                    return false
                }

                LayoutLayerConditionValue.REQUIRE -> if (current != true) {
                    return false
                }
            }
        }
        return true
    }

    operator fun get(key: LayoutLayerConditionKey): LayoutLayerConditionValue? = conditions[key]
}

fun layoutLayerConditionOf(vararg pairs: Pair<LayoutLayerConditionKey, LayoutLayerConditionValue>) =
    LayoutLayerCondition(persistentMapOf(*pairs))

@Serializable
data class LayoutLayer(
    val name: String = "",
    val widgets: PersistentList<ControllerWidget> = persistentListOf(),
    val condition: LayoutLayerCondition = LayoutLayerCondition(),
)

typealias TouchControllerLayout = PersistentList<LayoutLayer>

val defaultTouchControllerLayout: TouchControllerLayout = persistentListOf(
    LayoutLayer(
        name = "Control",
        condition = layoutLayerConditionOf(),
        widgets = persistentListOf(
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
            InventoryButton(),
        )
    ),
    LayoutLayer(
        name = "Normal",
        condition = layoutLayerConditionOf(
            LayoutLayerConditionKey.SWIMMING to LayoutLayerConditionValue.NEVER,
            LayoutLayerConditionKey.FLYING to LayoutLayerConditionValue.NEVER,
            LayoutLayerConditionKey.ON_MINECART to LayoutLayerConditionValue.NEVER,
            LayoutLayerConditionKey.ON_BOAT to LayoutLayerConditionValue.NEVER,
            LayoutLayerConditionKey.ON_PIG to LayoutLayerConditionValue.NEVER,
            LayoutLayerConditionKey.RIDING to LayoutLayerConditionValue.NEVER,
        ),
        widgets = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(8, 8),
                opacity = 0.6f,
                extraButton = DPadExtraButton.SNEAK,
            ),
            JumpButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 68),
                opacity = 0.6f,
            ),
            AscendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 116),
                opacity = 0.6f,
                texture = AscendButtonTexture.CLASSIC,
            ),
            DescendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 20),
                opacity = 0.6f,
                texture = DescendButtonTexture.CLASSIC,
            ),
        )
    ),
    LayoutLayer(
        name = "Swimming or Flying",
        condition = layoutLayerConditionOf(
            LayoutLayerConditionKey.ON_MINECART to LayoutLayerConditionValue.NEVER,
            LayoutLayerConditionKey.ON_BOAT to LayoutLayerConditionValue.NEVER,
            LayoutLayerConditionKey.ON_PIG to LayoutLayerConditionValue.NEVER,
            LayoutLayerConditionKey.RIDING to LayoutLayerConditionValue.NEVER,
        ),
        widgets = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(8, 8),
                opacity = 0.6f,
                extraButton = DPadExtraButton.NONE,
            ),
            JumpButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 68),
                opacity = 0.6f,
                texture = JumpButtonTexture.CLASSIC_FLYING,
            ),
            AscendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 116),
                opacity = 0.6f,
            ),
            DescendButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 20),
                opacity = 0.6f,
            ),
        )
    )
)