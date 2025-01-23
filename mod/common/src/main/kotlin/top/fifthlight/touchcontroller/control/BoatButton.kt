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
import top.fifthlight.touchcontroller.layout.BoatButton
import top.fifthlight.touchcontroller.layout.Context
import kotlin.math.round

@Serializable
enum class BoatButtonSide {
    @SerialName("left")
    LEFT,

    @SerialName("right")
    RIGHT
}

@Serializable
@SerialName("boat_button")
data class BoatButton(
    val size: Float = 3f,
    val side: BoatButtonSide = BoatButtonSide.LEFT,
    val classic: Boolean = true,
    override val align: Align = Align.LEFT_BOTTOM,
    override val offset: IntOffset = IntOffset.ZERO,
    override val opacity: Float = 1f
) : ControllerWidget() {
    companion object : KoinComponent {
        private val textFactory: TextFactory by inject()

        @Suppress("UNCHECKED_CAST")
        private val _properties = baseProperties + persistentListOf<Property<BoatButton, *>>(
            FloatProperty(
                getValue = { it.size },
                setValue = { config, value -> config.copy(size = value) },
                range = .5f..4f,
                messageFormatter = {
                    textFactory.format(
                        Texts.SCREEN_OPTIONS_WIDGET_BOAT_BUTTON_PROPERTY_SIZE,
                        round(it * 100f).toString()
                    )
                },
            ),
            EnumProperty(
                getValue = { it.side },
                setValue = { config, value -> config.copy(side = value) },
                name = textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_BOAT_BUTTON_PROPERTY_SIDE),
                items = persistentListOf(
                    BoatButtonSide.LEFT to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_BOAT_BUTTON_PROPERTY_SIDE_LEFT),
                    BoatButtonSide.RIGHT to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_BOAT_BUTTON_PROPERTY_SIDE_RIGHT),
                )
            ),
            BooleanProperty(
                getValue = { it.classic },
                setValue = { config, value -> config.copy(classic = value) },
                message = textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_BOAT_BUTTON_PROPERTY_CLASSIC),
            ),
        ) as PersistentList<Property<ControllerWidget, *>>
    }

    override val properties
        get() = _properties

    override fun size(): IntSize = IntSize((size * 22).toInt())

    override fun layout(context: Context) {
        context.BoatButton(this)
    }

    override fun cloneBase(align: Align, offset: IntOffset, opacity: Float) = copy(
        align = align,
        offset = offset,
        opacity = opacity
    )
}