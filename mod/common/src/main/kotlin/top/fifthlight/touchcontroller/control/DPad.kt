package top.fifthlight.touchcontroller.control

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.assets.Texts
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
    val extraButtonSize: Int = 18,
    override val align: Align = Align.LEFT_BOTTOM,
    override val offset: IntOffset = IntOffset.ZERO,
    override val opacity: Float = 1f
) : ControllerWidget() {
    companion object : KoinComponent {
        private val textFactory: TextFactory by inject()

        private val _properties = baseProperties + persistentListOf<Property<DPad, *>>(
            EnumProperty(
                getValue = { it.extraButton },
                setValue = { config, value -> config.copy(extraButton = value) },
                items = listOf(
                    DPadExtraButton.NONE to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_DPAD_PROPERTY_EXTRA_BUTTON_NONE),
                    DPadExtraButton.SNEAK to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_DPAD_PROPERTY_EXTRA_BUTTON_SNEAK),
                    DPadExtraButton.JUMP to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_DPAD_PROPERTY_EXTRA_BUTTON_JUMP),
                ),
            ),
            FloatProperty(
                getValue = { it.size },
                setValue = { config, value -> config.copy(size = value) },
                range = .5f..4f,
                messageFormatter = {
                    textFactory.format(
                        Texts.SCREEN_OPTIONS_WIDGET_DPAD_PROPERTY_SIZE,
                        round(it * 100f).toString()
                    )
                },
            ),
            IntProperty(
                getValue = { it.padding },
                setValue = { config, value -> config.copy(padding = value) },
                range = -1..16,
                messageFormatter = { textFactory.format(Texts.SCREEN_OPTIONS_WIDGET_DPAD_PROPERTY_PADDING, it) }
            ),
            IntProperty(
                getValue = { it.extraButtonSize },
                setValue = { config, value -> config.copy(extraButtonSize = value) },
                range = 12..22,
                messageFormatter = {
                    textFactory.format(
                        Texts.SCREEN_OPTIONS_WIDGET_DPAD_PROPERTY_EXTRA_BUTTON_SIZE,
                        it
                    )
                }
            ),
            BooleanProperty(
                getValue = { it.classic },
                setValue = { config, value -> config.copy(classic = value) },
                message = textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_DPAD_PROPERTY_CLASSIC),
            )
        )
    }

    override val properties
        get() = _properties

    fun buttonSize() = IntSize(((22 + padding) * size).toInt())
    fun buttonDisplaySize() = IntSize((22 * size).toInt())
    fun smallButtonDisplaySize() = IntSize((18 * size).toInt())
    fun extraButtonDisplaySize() = IntSize((extraButtonSize * size).toInt())

    override fun size(): IntSize = buttonSize() * 3

    override fun layout(context: Context) = context.DPad(this@DPad)

    override fun cloneBase(align: Align, offset: IntOffset, opacity: Float) = copy(
        align = align,
        offset = offset,
        opacity = opacity
    )
}
