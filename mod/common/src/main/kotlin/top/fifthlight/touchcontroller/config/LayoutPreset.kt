package top.fifthlight.touchcontroller.config

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import top.fifthlight.data.IntOffset
import top.fifthlight.touchcontroller.control.*
import top.fifthlight.touchcontroller.ext.LayoutPresetsSerializer
import top.fifthlight.touchcontroller.layout.Align

val defaultControllerLayout: ControllerLayout = controllerLayoutOf(
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
            LayerConditionKey.SWIMMING to LayerConditionValue.NEVER,
            LayerConditionKey.FLYING to LayerConditionValue.NEVER,
            LayerConditionKey.RIDING to LayerConditionValue.NEVER,
        ),
        widgets = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(8, 8),
                opacity = 0.6f,
                extraButton = DPadExtraButton.SNEAK_DOUBLE_CLICK,
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
            LayerConditionKey.SWIMMING to LayerConditionValue.WANT,
            LayerConditionKey.FLYING to LayerConditionValue.WANT,
            LayerConditionKey.RIDING to LayerConditionValue.NEVER,
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
            LayerConditionKey.ON_MINECART to LayerConditionValue.REQUIRE,
        ),
        widgets = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(8, 8),
                opacity = 0.6f,
                extraButton = DPadExtraButton.DISMOUNT_DOUBLE_CLICK,
            ),
        )
    ),
    LayoutLayer(
        name = "On pig or strider",
        condition = layoutLayerConditionOf(
            LayerConditionKey.ON_STRIDER to LayerConditionValue.WANT,
            LayerConditionKey.ON_STRIDER to LayerConditionValue.WANT,
        ),
        widgets = persistentListOf(
            SneakButton(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(68, 68),
                opacity = 0.6f,
                texture = SneakButtonTexture.DISMOUNT,
                trigger = SneakButtonTrigger.DOUBLE_CLICK_TRIGGER,
            ),
        )
    ),
    LayoutLayer(
        name = "On horse or donkey",
        condition = layoutLayerConditionOf(
            LayerConditionKey.ON_HORSE to LayerConditionValue.WANT,
            LayerConditionKey.ON_DONKEY to LayerConditionValue.WANT,
        ),
        widgets = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(8, 8),
                opacity = 0.6f,
                extraButton = DPadExtraButton.DISMOUNT_DOUBLE_CLICK,
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
            LayerConditionKey.ON_HORSE to LayerConditionValue.REQUIRE,
        ),
        widgets = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(8, 8),
                opacity = 0.6f,
                extraButton = DPadExtraButton.DISMOUNT_DOUBLE_CLICK,
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
            LayerConditionKey.ON_MINECART to LayerConditionValue.NEVER,
            LayerConditionKey.ON_BOAT to LayerConditionValue.NEVER,
            LayerConditionKey.ON_PIG to LayerConditionValue.NEVER,
            LayerConditionKey.ON_HORSE to LayerConditionValue.NEVER,
            LayerConditionKey.ON_DONKEY to LayerConditionValue.NEVER,
            LayerConditionKey.ON_LLAMA to LayerConditionValue.NEVER,
            LayerConditionKey.ON_STRIDER to LayerConditionValue.NEVER,
            LayerConditionKey.RIDING to LayerConditionValue.REQUIRE,
        ),
        widgets = persistentListOf(
            DPad(
                align = Align.LEFT_BOTTOM,
                offset = IntOffset(8, 8),
                opacity = 0.6f,
                extraButton = DPadExtraButton.DISMOUNT_DOUBLE_CLICK,
            ),
            JumpButton(
                align = Align.RIGHT_BOTTOM,
                offset = IntOffset(42, 68),
                opacity = 0.6f,
            ),
        )
    ),
)

@Immutable
@Serializable
data class LayoutPreset(
    val name: String = "Unnamed preset",
    val layout: ControllerLayout = ControllerLayout(),
    @Transient
    val default: Boolean = false,
)

@JvmInline
@Serializable(with = LayoutPresetsSerializer::class)
value class LayoutPresets(
    val presets: PersistentList<LayoutPreset> = persistentListOf(),
)

fun layoutPresetsOf(vararg pairs: LayoutPreset) = LayoutPresets(persistentListOf(*pairs))

val defaultPresets = layoutPresetsOf(
    LayoutPreset(
        name = "Default",
        layout = defaultControllerLayout,
        default = true,
    ),
)
