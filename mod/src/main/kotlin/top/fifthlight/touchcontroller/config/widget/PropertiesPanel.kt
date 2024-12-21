package top.fifthlight.touchcontroller.config.widget

import kotlinx.collections.immutable.PersistentList
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.widget.ElementListWidget
import top.fifthlight.data.IntSize
import top.fifthlight.touchcontroller.config.ObservableValue
import top.fifthlight.touchcontroller.config.TouchControllerLayout
import top.fifthlight.touchcontroller.config.replaceItem
import top.fifthlight.touchcontroller.control.ControllerWidget
import top.fifthlight.touchcontroller.control.ControllerWidget.Property
import top.fifthlight.touchcontroller.control.ControllerWidget.PropertyWidget

class PropertiesPanelEntry<Property : ControllerWidget.Property<Config, *, Widget>, Widget : ClickableWidget, Config : ControllerWidget>(
    initialConfig: Config,
    widgetSize: IntSize,
    property: Property,
    private val propertyEditProvider: ControllerWidget.PropertyEditProvider<Config>,
) : ElementListWidget.Entry<PropertiesPanelEntry<*, *, *>>() {
    private val propertyWidget: PropertyWidget<Config, Widget> = property.createController(propertyEditProvider)
    private val widget: Widget = propertyWidget.createWidget(initialConfig, widgetSize)

    fun update() {
        propertyWidget.updateWidget(propertyEditProvider.currentConfig, widget)
    }

    override fun render(
        context: DrawContext,
        index: Int,
        y: Int,
        x: Int,
        entryWidth: Int,
        entryHeight: Int,
        mouseX: Int,
        mouseY: Int,
        hovered: Boolean,
        tickDelta: Float
    ) {
        widget.setPosition(x, y)
        widget.setDimensions(entryWidth, entryHeight)
        widget.render(context, mouseX, mouseY, tickDelta)
    }

    override fun children(): List<Element> = listOf(widget)

    override fun selectableChildren(): List<Selectable> = listOf(widget)
}

class PropertiesPanel(
    client: MinecraftClient,
    width: Int,
    height: Int = 0,
    y: Int = 0,
    itemHeight: Int,
    private val itemPadding: Int,
    private val layoutConfig: ObservableValue<TouchControllerLayout>,
    private val selectedConfig: ObservableValue<ControllerWidget?>,
) : ElementListWidget<PropertiesPanelEntry<*, *, *>>(
    client, width, height, y, itemHeight,
) {
    private fun <Config : ControllerWidget, Widget : ClickableWidget> createEntry(
        config: Config,
        property: Property<Config, *, Widget>
    ) =
        PropertiesPanelEntry(
            initialConfig = config,
            widgetSize = IntSize(rowWidth, itemHeight),
            property = property,
            propertyEditProvider = object : ControllerWidget.PropertyEditProvider<Config> {
                @Suppress("UNCHECKED_CAST")
                override val currentConfig
                    get() = selectedConfig.value as Config

                override fun newConfig(config: Config) {
                    layoutConfig.replaceItem(oldItem = selectedConfig.value ?: return, newItem = config)
                    selectedConfig.value = config
                }
            }
        )

    private var selectedProperties: PersistentList<Property<ControllerWidget, *, *>>? = null

    init {
        selectedConfig.addListener { newConfig ->
            val properties = newConfig?.properties
            if (selectedProperties != properties) {
                clearEntries()
                selectedProperties = properties
                val config = newConfig ?: return@addListener
                properties?.forEach {
                    addEntry(createEntry(config, it))
                }
            } else {
                children().forEach(PropertiesPanelEntry<*, *, *>::update)
            }
        }
    }

    override fun getRowWidth(): Int = width - itemPadding
}