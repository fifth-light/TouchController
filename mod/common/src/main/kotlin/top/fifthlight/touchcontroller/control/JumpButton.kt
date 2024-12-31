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
import top.fifthlight.touchcontroller.layout.JumpButton
import kotlin.math.round

enum class JumpButtonTexture {
    CLASSIC,
    CLASSIC_FLYING,
    NEW,
}

@Serializable
@SerialName("jump_button")
data class JumpButton(
    val size: Float = 2f,
    val texture: JumpButtonTexture = JumpButtonTexture.CLASSIC,
    override val align: Align = Align.RIGHT_BOTTOM,
    override val offset: IntOffset = IntOffset.ZERO,
    override val opacity: Float = 1f
) : ControllerWidget() {
    companion object : KoinComponent {
        private val textFactory: TextFactory by inject()

        @Suppress("UNCHECKED_CAST")
        private val _properties = baseProperties + persistentListOf<Property<JumpButton, *>>(
            FloatProperty(
                getValue = { it.size },
                setValue = { config, value -> config.copy(size = value) },
                range = .5f..4f,
                messageFormatter = {
                    textFactory.format(
                        Texts.SCREEN_OPTIONS_WIDGET_JUMP_BUTTON_PROPERTY_SIZE,
                        round(it * 100f).toString()
                    )
                },
            ),
            EnumProperty(
                getValue = { it.texture },
                setValue = { config, value -> config.copy(texture = value) },
                name = textFactory.literal("Style"),
                items = persistentListOf(
                    JumpButtonTexture.CLASSIC to textFactory.literal("Classic"),
                    JumpButtonTexture.CLASSIC_FLYING to textFactory.literal("Classic flying"),
                    JumpButtonTexture.NEW to textFactory.literal("New"),
                )
            )
        ) as PersistentList<Property<ControllerWidget, *>>
    }

    override val properties
        get() = _properties

    private val textureSize
        get() = if (texture == JumpButtonTexture.NEW) 22 else 18

    override fun size(): IntSize = IntSize((size * textureSize).toInt())

    override fun layout(context: Context) {
        context.JumpButton(this@JumpButton)
    }

    override fun cloneBase(align: Align, offset: IntOffset, opacity: Float) = copy(
        align = align,
        offset = offset,
        opacity = opacity
    )
}