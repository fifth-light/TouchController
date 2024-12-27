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
import top.fifthlight.touchcontroller.layout.Joystick
import kotlin.math.round

@Serializable
@SerialName("joystick")
data class Joystick(
    val size: Float = 1f,
    val stickSize: Float = 1f,
    val triggerSprint: Boolean = false,
    val increaseOpacityWhenActive: Boolean = true,
    override val align: Align = Align.LEFT_BOTTOM,
    override val offset: IntOffset = IntOffset.ZERO,
    override val opacity: Float = 1f
) : ControllerWidget() {
    companion object : KoinComponent {
        private val textFactory: TextFactory by inject()

        private val _properties = baseProperties + persistentListOf<Property<Joystick, *>>(
            FloatProperty(
                getValue = { it.size },
                setValue = { config, value -> config.copy(size = value) },
                range = .5f..4f,
                messageFormatter = {
                    textFactory.format(
                        Texts.SCREEN_OPTIONS_WIDGET_JOYSTICK_PROPERTY_SIZE,
                        round(it * 100f).toString()
                    )
                },
            ),
            FloatProperty(
                getValue = { it.stickSize },
                setValue = { config, value -> config.copy(stickSize = value) },
                range = .5f..4f,
                messageFormatter = {
                    textFactory.format(
                        Texts.SCREEN_OPTIONS_WIDGET_JOYSTICK_PROPERTY_STICK_SIZE,
                        round(it * 100f).toString()
                    )
                },
            ),
            BooleanProperty(
                getValue = { it.triggerSprint },
                setValue = { config, value -> config.copy(triggerSprint = value) },
                message = textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_JOYSTICK_PROPERTY_TRIGGER_SPRINT),
            ),
            BooleanProperty(
                getValue = { it.increaseOpacityWhenActive },
                setValue = { config, value -> config.copy(increaseOpacityWhenActive = value) },
                message = textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_JOYSTICK_PROPERTY_INCREASE_OPACITY_WHEN_ACTIVE),
            )
        )
    }

    override val properties
        get() = _properties

    override fun size(): IntSize = IntSize((size * 72).toInt())

    fun stickSize() = IntSize((stickSize * 48).toInt())

    override fun layout(context: Context) {
        context.Joystick(this@Joystick)
    }

    override fun cloneBase(align: Align, offset: IntOffset, opacity: Float) = copy(
        align = align,
        offset = offset,
        opacity = opacity
    )
}