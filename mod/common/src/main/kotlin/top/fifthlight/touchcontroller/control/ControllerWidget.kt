package top.fifthlight.touchcontroller.control

import androidx.compose.runtime.Composable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.combine.modifier.Modifier
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.layout.Align
import top.fifthlight.touchcontroller.layout.Context
import kotlin.math.round

@Serializable
sealed class ControllerWidget {
    abstract val align: Align
    abstract val offset: IntOffset
    abstract val opacity: Float

    interface Property<Config : ControllerWidget, Value> {
        @Composable
        fun controller(
            modifier: Modifier = Modifier,
            config: ControllerWidget,
            onConfigChanged: (ControllerWidget) -> Unit
        )
    }

    companion object : KoinComponent {
        private val textFactory: TextFactory by inject()

        val baseProperties = persistentListOf<Property<ControllerWidget, *>>(
            EnumProperty(
                getValue = { it.align },
                setValue = { config, value -> config.cloneBase(align = value) },
                name = textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_GENERAL_PROPERTY_ANCHOR_NAME),
                items = listOf(
                    Align.LEFT_TOP to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_GENERAL_PROPERTY_ANCHOR_TOP_LEFT),
                    Align.LEFT_CENTER to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_GENERAL_PROPERTY_ANCHOR_CENTER_LEFT),
                    Align.LEFT_BOTTOM to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_GENERAL_PROPERTY_ANCHOR_BOTTOM_LEFT),
                    Align.CENTER_TOP to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_GENERAL_PROPERTY_ANCHOR_TOP_CENTER),
                    Align.CENTER_CENTER to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_GENERAL_PROPERTY_ANCHOR_CENTER_CENTER),
                    Align.CENTER_BOTTOM to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_GENERAL_PROPERTY_ANCHOR_BOTTOM_CENTER),
                    Align.RIGHT_TOP to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_GENERAL_PROPERTY_ANCHOR_TOP_RIGHT),
                    Align.RIGHT_CENTER to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_GENERAL_PROPERTY_ANCHOR_CENTER_RIGHT),
                    Align.RIGHT_BOTTOM to textFactory.of(Texts.SCREEN_OPTIONS_WIDGET_GENERAL_PROPERTY_ANCHOR_BOTTOM_RIGHT),
                ),
            ),
            FloatProperty(
                getValue = { it.opacity },
                setValue = { config, value -> config.cloneBase(opacity = value) },
                messageFormatter = { opacity ->
                    textFactory.format(
                        Texts.SCREEN_OPTIONS_WIDGET_GENERAL_PROPERTY_OPACITY,
                        round(opacity * 100f).toInt().toString()
                    )
                }
            )
        )
    }

    @Transient
    open val properties: PersistentList<Property<ControllerWidget, *>> = baseProperties

    abstract fun size(): IntSize

    abstract fun layout(context: Context)

    abstract fun cloneBase(
        align: Align = this.align,
        offset: IntOffset = this.offset,
        opacity: Float = this.opacity,
    ): ControllerWidget
}
