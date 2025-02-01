package top.fifthlight.touchcontroller.control

import kotlinx.collections.immutable.PersistentList
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
import top.fifthlight.touchcontroller.layout.PerspectiveSwitchButton
import kotlin.math.round

@Serializable
enum class PerspectiveSwitchButtonStyle {
    @SerialName("classic")
    CLASSIC,

    @SerialName("classic_simple")
    CLASSIC_SIMPLE,

    @SerialName("new")
    NEW,

    @SerialName("new_simple")
    NEW_SIMPLE
}

@Serializable
@SerialName("perspective_switch_button")
data class PerspectiveSwitchButton(
    val size: Float = 1f,
    val style: PerspectiveSwitchButtonStyle = PerspectiveSwitchButtonStyle.CLASSIC,
    override val align: Align = Align.CENTER_TOP,
    override val offset: IntOffset = IntOffset.ZERO,
    override val opacity: Float = 1f
) : ControllerWidget() {
    companion object : KoinComponent {
        private val textFactory: TextFactory by inject()

        @Suppress("UNCHECKED_CAST")
        private val _properties = baseProperties + persistentListOf<Property<PerspectiveSwitchButton, *>>(
            FloatProperty(
                getValue = { it.size },
                setValue = { config, value -> config.copy(size = value) },
                range = .5f..4f,
                messageFormatter = {
                    textFactory.format(
                        Texts.SCREEN_OPTIONS_WIDGET_PAUSE_BUTTON_PROPERTY_SIZE,
                        round(it * 100f).toString()
                    )
                }
            ),
            EnumProperty(
                getValue = { it.style },
                setValue = { config, value -> config.copy(style = value) },
                name = textFactory.format(Texts.SCREEN_OPTIONS_WIDGET_PERSPECTIVE_SWITCH_BUTTON_PROPERTY_STYLE),
                items = listOf(
                    PerspectiveSwitchButtonStyle.CLASSIC to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_PERSPECTIVE_SWITCH_BUTTON_PROPERTY_STYLE_CLASSIC),
                    PerspectiveSwitchButtonStyle.CLASSIC_SIMPLE to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_PERSPECTIVE_SWITCH_BUTTON_PROPERTY_STYLE_CLASSIC_SIMPLE),
                    PerspectiveSwitchButtonStyle.NEW to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_PERSPECTIVE_SWITCH_BUTTON_PROPERTY_STYLE_NEW),
                    PerspectiveSwitchButtonStyle.NEW_SIMPLE to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_PERSPECTIVE_SWITCH_BUTTON_PROPERTY_STYLE_NEW_SIMPLE),
                ),
            ),
        ) as PersistentList<Property<ControllerWidget, *>>
    }

    override val properties
        get() = _properties

    private val textureSize
        get() = 18

    override fun size(): IntSize = IntSize((size * textureSize).toInt())

    override fun layout(context: Context) {
        context.PerspectiveSwitchButton(config = this)
    }

    override fun cloneBase(align: Align, offset: IntOffset, opacity: Float) = copy(
        align = align,
        offset = offset,
        opacity = opacity
    )
}