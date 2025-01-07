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
import top.fifthlight.touchcontroller.layout.SneakButton
import kotlin.math.round

@Serializable
enum class SneakButtonTexture {
    @SerialName("classic")
    CLASSIC,

    @SerialName("new")
    NEW,

    @SerialName("new_dpad")
    NEW_DPAD,

    @SerialName("dismount")
    DISMOUNT,

    @SerialName("dismount_dpad")
    DISMOUNT_DPAD,
}

@Serializable
enum class SneakButtonTrigger {
    @SerialName("double_click_lock")
    DOUBLE_CLICK_LOCK,

    @SerialName("single_click_lock")
    SINGLE_CLICK_LOCK,

    @SerialName("hold")
    HOLD,

    @SerialName("single_click_trigger")
    SINGLE_CLICK_TRIGGER,
}

@Serializable
@SerialName("sneak_button")
data class SneakButton(
    val size: Float = 2f,
    val texture: SneakButtonTexture = SneakButtonTexture.CLASSIC,
    val trigger: SneakButtonTrigger = SneakButtonTrigger.DOUBLE_CLICK_LOCK,
    override val align: Align = Align.RIGHT_BOTTOM,
    override val offset: IntOffset = IntOffset.ZERO,
    override val opacity: Float = 1f
) : ControllerWidget() {
    companion object : KoinComponent {
        private val textFactory: TextFactory by inject()

        @Suppress("UNCHECKED_CAST")
        private val _properties = baseProperties + persistentListOf<Property<SneakButton, *>>(
            FloatProperty(
                getValue = { it.size },
                setValue = { config, value -> config.copy(size = value) },
                range = .5f..4f,
                messageFormatter = {
                    textFactory.format(
                        Texts.SCREEN_OPTIONS_WIDGET_SNEAK_BUTTON_PROPERTY_SIZE,
                        round(it * 100f).toString()
                    )
                },
            ),
            EnumProperty(
                getValue = { it.texture },
                setValue = { config, value -> config.copy(texture = value) },
                name = textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_SNEAK_BUTTON_PROPERTY_STYLE),
                items = listOf(
                    SneakButtonTexture.CLASSIC to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_SNEAK_BUTTON_PROPERTY_STYLE_CLASSIC),
                    SneakButtonTexture.NEW to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_SNEAK_BUTTON_PROPERTY_STYLE_NEW),
                    SneakButtonTexture.DISMOUNT to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_SNEAK_BUTTON_PROPERTY_STYLE_DISMOUNT),
                ),
            ),
            EnumProperty(
                getValue = { it.trigger },
                setValue = { config, value -> config.copy(trigger = value) },
                name = textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_SNEAK_BUTTON_PROPERTY_TRIGGER),
                items = listOf(
                    SneakButtonTrigger.DOUBLE_CLICK_LOCK to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_SNEAK_BUTTON_PROPERTY_TRIGGER_DOUBLE_CLICK_LOCK),
                    SneakButtonTrigger.SINGLE_CLICK_LOCK to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_SNEAK_BUTTON_PROPERTY_TRIGGER_SINGLE_CLICK_LOCK),
                    SneakButtonTrigger.HOLD to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_SNEAK_BUTTON_PROPERTY_TRIGGER_HOLD),
                    SneakButtonTrigger.SINGLE_CLICK_TRIGGER to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_SNEAK_BUTTON_PROPERTY_TRIGGER_SINGLE_CLICK_TRIGGER),
                ),
            ),
        ) as PersistentList<Property<ControllerWidget, *>>
    }

    override val properties
        get() = _properties

    private val textureSize
        get() = if (texture == SneakButtonTexture.CLASSIC) 18 else 22

    override fun size(): IntSize = IntSize((size * textureSize).toInt())

    override fun layout(context: Context) {
        context.SneakButton(this@SneakButton)
    }

    override fun cloneBase(align: Align, offset: IntOffset, opacity: Float) = copy(
        align = align,
        offset = offset,
        opacity = opacity
    )
}