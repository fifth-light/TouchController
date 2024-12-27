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
import top.fifthlight.touchcontroller.layout.InventoryButton
import kotlin.math.round

@Serializable
@SerialName("inventory_button")
data class InventoryButton(
    val size: Float = 1f,
    override val align: Align = Align.CENTER_BOTTOM,
    override val offset: IntOffset = IntOffset(101, 0),
    override val opacity: Float = 1f
) : ControllerWidget() {
    companion object : KoinComponent {
        private val textFactory: TextFactory by inject()

        private val _properties = baseProperties + persistentListOf<Property<InventoryButton, *>>(
            FloatProperty(
                getValue = { it.size },
                setValue = { config, value -> config.copy(size = value) },
                range = .5f..4f,
                messageFormatter = {
                    textFactory.format(
                        Texts.SCREEN_OPTIONS_WIDGET_CHAT_BUTTON_PROPERTY_SIZE,
                        round(it * 100f).toString()
                    )
                }
            )
        )
    }

    override val properties
        get() = _properties

    override fun size(): IntSize = IntSize((size * 22).toInt())

    override fun layout(context: Context) {
        context.InventoryButton()
    }

    override fun cloneBase(align: Align, offset: IntOffset, opacity: Float) = copy(
        align = align,
        offset = offset,
        opacity = opacity
    )

}