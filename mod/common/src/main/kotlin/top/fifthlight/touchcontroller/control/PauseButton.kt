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
import top.fifthlight.touchcontroller.layout.PauseButton
import kotlin.math.round

@Serializable
@SerialName("pause_button")
data class PauseButton(
    val size: Float = 1f,
    val classic: Boolean = true,
    override val align: Align = Align.CENTER_TOP,
    override val offset: IntOffset = IntOffset.ZERO,
    override val opacity: Float = 1f
) : ControllerWidget() {
    companion object : KoinComponent {
        private val textFactory: TextFactory by inject()

        private val _properties = baseProperties + persistentListOf<Property<PauseButton, *>>(
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
            BooleanProperty(
                getValue = { it.classic },
                setValue = { config, value -> config.copy(classic = value) },
                message = textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_PAUSE_BUTTON_PROPERTY_CLASSIC),
            )
        )
    }

    override val properties
        get() = _properties

    private val textureSize
        get() = 18

    override fun size(): IntSize = IntSize((size * textureSize).toInt())

    override fun layout(context: Context) {
        context.PauseButton(config = this)
    }

    override fun cloneBase(align: Align, offset: IntOffset, opacity: Float) = copy(
        align = align,
        offset = offset,
        opacity = opacity
    )
}