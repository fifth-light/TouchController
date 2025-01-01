package top.fifthlight.touchcontroller.config

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable
import top.fifthlight.data.IntOffset
import top.fifthlight.touchcontroller.control.*
import top.fifthlight.touchcontroller.ext.LayoutLayerConditionSerializer
import top.fifthlight.touchcontroller.ext.LayoutLayerSerializer
import top.fifthlight.touchcontroller.layout.Align

enum class LayoutLayerConditionValue {
    NEVER,
    WANT,
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

@Serializable(with = LayoutLayerConditionSerializer::class)
@JvmInline
value class LayoutLayerCondition(
    val conditions: PersistentMap<LayoutLayerConditionKey, LayoutLayerConditionValue> = persistentMapOf()
) {
    fun check(currentState: PersistentMap<LayoutLayerConditionKey, Boolean>): Boolean {
        var haveWant = false
        var haveFulfilledWant = false
        for (condition in conditions) {
            val current = currentState[condition.key]
            when (condition.value) {
                LayoutLayerConditionValue.NEVER -> if (current == true) {
                    return false
                }

                LayoutLayerConditionValue.WANT -> {
                    haveWant = true
                    if (current == true) {
                        haveFulfilledWant = true
                    }
                }

                LayoutLayerConditionValue.REQUIRE -> if (current != true) {
                    return false
                }
            }
        }
        if (haveWant && !haveFulfilledWant) {
            return false
        }
        return true
    }

    operator fun get(key: LayoutLayerConditionKey): LayoutLayerConditionValue? = conditions[key]
    fun set(key: LayoutLayerConditionKey, value: LayoutLayerConditionValue?) = LayoutLayerCondition(
        if (value == null) {
            conditions.remove(key)
        } else {
            conditions.put(key, value)
        }
    )
}

fun layoutLayerConditionOf(vararg pairs: Pair<LayoutLayerConditionKey, LayoutLayerConditionValue>) =
    LayoutLayerCondition(persistentMapOf(*pairs))

@Serializable(with = LayoutLayerSerializer::class)
data class LayoutLayer(
    val name: String = "Unnamed layer",
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
        )
    ),
    LayoutLayer(
        name = "Swimming or flying",
        condition = layoutLayerConditionOf(
            LayoutLayerConditionKey.SWIMMING to LayoutLayerConditionValue.WANT,
            LayoutLayerConditionKey.FLYING to LayoutLayerConditionValue.WANT,
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
    ),
    LayoutLayer(
        name = "On minecart",
        condition = layoutLayerConditionOf(
            LayoutLayerConditionKey.ON_MINECART to LayoutLayerConditionValue.REQUIRE,
        ),
        widgets = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(8, 8),
                opacity = 0.6f,
                extraButton = DPadExtraButton.SNEAK,
            ),
        )
    ),
    LayoutLayer(
        name = "On pig or strider",
        condition = layoutLayerConditionOf(
            LayoutLayerConditionKey.ON_STRIDER to LayoutLayerConditionValue.WANT,
            LayoutLayerConditionKey.ON_STRIDER to LayoutLayerConditionValue.WANT,
        ),
        widgets = persistentListOf(
            SneakButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(68, 68),
                opacity = 0.6f,
            ),
        )
    ),
    LayoutLayer(
        name = "On horse or donkey",
        condition = layoutLayerConditionOf(
            LayoutLayerConditionKey.ON_HORSE to LayoutLayerConditionValue.WANT,
            LayoutLayerConditionKey.ON_DONKEY to LayoutLayerConditionValue.WANT,
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
        )
    ),
    LayoutLayer(
        name = "On llama",
        condition = layoutLayerConditionOf(
            LayoutLayerConditionKey.ON_HORSE to LayoutLayerConditionValue.REQUIRE,
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
        )
    ),
    LayoutLayer(
        name = "Riding on entity",
        condition = layoutLayerConditionOf(
            LayoutLayerConditionKey.ON_MINECART to LayoutLayerConditionValue.NEVER,
            LayoutLayerConditionKey.ON_BOAT to LayoutLayerConditionValue.NEVER,
            LayoutLayerConditionKey.ON_PIG to LayoutLayerConditionValue.NEVER,
            LayoutLayerConditionKey.ON_HORSE to LayoutLayerConditionValue.NEVER,
            LayoutLayerConditionKey.ON_DONKEY to LayoutLayerConditionValue.NEVER,
            LayoutLayerConditionKey.ON_LLAMA to LayoutLayerConditionValue.NEVER,
            LayoutLayerConditionKey.ON_STRIDER to LayoutLayerConditionValue.NEVER,
            LayoutLayerConditionKey.RIDING to LayoutLayerConditionValue.REQUIRE,
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
        )
    ),
)