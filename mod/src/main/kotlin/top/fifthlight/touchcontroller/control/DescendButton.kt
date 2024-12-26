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
import top.fifthlight.touchcontroller.layout.DescendButton
import kotlin.math.round

enum class DescendButtonTexture {
    CLASSIC,
    SWIMMING,
    FLYING,
}

@Serializable
@SerialName("descend_button")
data class DescendButton(
    val size: Float = 2f,
    val texture: DescendButtonTexture = DescendButtonTexture.CLASSIC,
    override val align: Align = Align.RIGHT_BOTTOM,
    override val offset: IntOffset = IntOffset.ZERO,
    override val opacity: Float = 1f,
) : ControllerWidget() {
    companion object {
        private val _properties = persistentListOf<Property<DescendButton, *, *>>(
            FloatProperty(
                getValue = { it.size },
                setValue = { config, value -> config.copy(size = value) },
                startValue = .5f,
                endValue = 4f,
                messageFormatter = {
                    Text.translatable(
                        Texts.OPTIONS_WIDGET_DESCEND_BUTTON_PROPERTY_SIZE,
                        round(it * 100f).toString()
                    )
                }
            ),
            EnumProperty(
                getValue = { it.texture },
                setValue = { config, value -> config.copy(texture = value) },
                items = persistentListOf(
                    DescendButtonTexture.CLASSIC to Text.literal("Classic"),
                    DescendButtonTexture.SWIMMING to Text.literal("Swimming"),
                    DescendButtonTexture.FLYING to Text.literal("Flying"),
                )
            )
        )

    }

    @Suppress("UNCHECKED_CAST")
    @Transient
    override val properties = super.properties + _properties as PersistentList<Property<ControllerWidget, *, *>>

    private val textureSize
        get() = if (texture == DescendButtonTexture.CLASSIC) 18 else 22

    override fun size(): IntSize = IntSize((size * textureSize).toInt())

    override fun layout(context: Context) {
        context.DescendButton(config = this)
    }

    override fun cloneBase(align: Align, offset: IntOffset, opacity: Float) = copy(
        align = align,
        offset = offset,
        opacity = opacity
    )
}