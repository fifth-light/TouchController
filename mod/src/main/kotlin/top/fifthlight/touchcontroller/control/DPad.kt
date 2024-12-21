package top.fifthlight.touchcontroller.control

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.text.Text
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.asset.Texts
import top.fifthlight.touchcontroller.layout.Align
import top.fifthlight.touchcontroller.layout.Context
import top.fifthlight.touchcontroller.layout.DPad
import kotlin.math.round

enum class DPadExtraButton {
    NONE,
    SNEAK,
    JUMP
}

@Serializable
@SerialName("dpad")
data class DPad(
    val classic: Boolean = true,
    val size: Float = 2f,
    val padding: Int = if (classic) 4 else -1,
    val extraButton: DPadExtraButton = DPadExtraButton.SNEAK,
    override val align: Align = Align.LEFT_BOTTOM,
    override val offset: IntOffset = IntOffset.ZERO,
    override val opacity: Float = 1f
) : ControllerWidget() {
    companion object {
        private val _properties = persistentListOf<Property<DPad, *, *>>(
            EnumProperty(
                getValue = { it.extraButton },
                setValue = { config, value -> config.copy(extraButton = value) },
                items = listOf(
                    DPadExtraButton.NONE to Texts.OPTIONS_WIDGET_DPAD_PROPERTY_EXTRA_BUTTON_NONE,
                    DPadExtraButton.SNEAK to Texts.OPTIONS_WIDGET_DPAD_PROPERTY_EXTRA_BUTTON_SNEAK,
                    DPadExtraButton.JUMP to Texts.OPTIONS_WIDGET_DPAD_PROPERTY_EXTRA_BUTTON_JUMP,
                ),
            ),
            FloatProperty(
                getValue = { it.size },
                setValue = { config, value -> config.copy(size = value) },
                startValue = .5f,
                endValue = 4f,
                messageFormatter = {
                    Text.translatable(
                        Texts.OPTIONS_WIDGET_DPAD_PROPERTY_SIZE,
                        round(it * 100f).toString()
                    )
                },
            ),
            IntProperty(
                getValue = { it.padding },
                setValue = { config, value -> config.copy(padding = value) },
                range = -1..16,
                messageFormatter = { Text.translatable(Texts.OPTIONS_WIDGET_DPAD_PROPERTY_PADDING, it) }
            ),
            BooleanProperty(
                getValue = { it.classic },
                setValue = { config, value -> config.copy(classic = value) },
                message = Texts.OPTIONS_WIDGET_DPAD_PROPERTY_CLASSIC,
            )
        )
    }

    @Suppress("UNCHECKED_CAST")
    @Transient
    override val properties = super.properties + _properties as PersistentList<Property<ControllerWidget, *, *>>

    fun buttonSize() = IntSize(((22 + padding) * size).toInt())
    fun largeDisplaySize() = IntSize((22 * size).toInt())
    fun smallDisplaySize() = IntSize((18 * size).toInt())

    override fun size(): IntSize = buttonSize() * 3

    override fun layout(context: Context) = context.DPad(this@DPad)

    override fun cloneBase(align: Align, offset: IntOffset, opacity: Float) = copy(
        align = align,
        offset = offset,
        opacity = opacity
    )
}
