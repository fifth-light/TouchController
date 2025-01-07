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
import top.fifthlight.touchcontroller.layout.AscendButton
import top.fifthlight.touchcontroller.layout.Context
import kotlin.math.round

@Serializable
enum class AscendButtonTexture {
    @SerialName("classic")
    CLASSIC,

    @SerialName("swimming")
    SWIMMING,

    @SerialName("flying")
    FLYING,
}

@Serializable
@SerialName("ascend_button")
data class AscendButton(
    val size: Float = 2f,
    val texture: AscendButtonTexture = AscendButtonTexture.CLASSIC,
    override val align: Align = Align.RIGHT_BOTTOM,
    override val offset: IntOffset = IntOffset.ZERO,
    override val opacity: Float = 1f
) : ControllerWidget() {
    companion object : KoinComponent {
        private val textFactory: TextFactory by inject()

        @Suppress("UNCHECKED_CAST")
        private val _properties = baseProperties + persistentListOf<Property<AscendButton, *>>(
            FloatProperty(
                getValue = { it.size },
                setValue = { config, value -> config.copy(size = value) },
                range = .5f..4f,
                messageFormatter = {
                    textFactory.format(
                        Texts.SCREEN_OPTIONS_WIDGET_ASCEND_BUTTON_PROPERTY_SIZE,
                        round(it * 100f).toString()
                    )
                }
            ),
            EnumProperty(
                getValue = { it.texture },
                setValue = { config, value -> config.copy(texture = value) },
                name = textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_ASCEND_BUTTON_PROPERTY_STYLE),
                items = persistentListOf(
                    AscendButtonTexture.CLASSIC to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_ASCEND_BUTTON_PROPERTY_STYLE_CLASSIC),
                    AscendButtonTexture.SWIMMING to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_ASCEND_BUTTON_PROPERTY_STYLE_SWIMMING),
                    AscendButtonTexture.FLYING to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_ASCEND_BUTTON_PROPERTY_STYLE_FLYING),
                )
            )
        ) as PersistentList<Property<ControllerWidget, *>>
    }

    override val properties
        get() = _properties

    private val textureSize
        get() = if (texture == AscendButtonTexture.CLASSIC) 18 else 22

    override fun size(): IntSize = IntSize((size * textureSize).toInt())

    override fun layout(context: Context) {
        context.AscendButton(config = this)
    }

    override fun cloneBase(align: Align, offset: IntOffset, opacity: Float) = copy(
        align = align,
        offset = offset,
        opacity = opacity
    )
}