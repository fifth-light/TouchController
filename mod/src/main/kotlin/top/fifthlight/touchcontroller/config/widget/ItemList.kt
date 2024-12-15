package top.fifthlight.touchcontroller.config.widget

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.AbstractWidget
import dev.isxander.yacl3.gui.YACLScreen
import dev.isxander.yacl3.gui.controllers.ControllerWidget
import kotlinx.collections.immutable.toPersistentList
import net.minecraft.client.util.InputUtil
import net.minecraft.item.Item
import net.minecraft.text.Text
import top.fifthlight.touchcontroller.asset.Texts
import top.fifthlight.touchcontroller.config.ItemsListScreen
import top.fifthlight.touchcontroller.config.ObservableValue

class ItemsListController(
    private val option: Option<List<Item>>
) : Controller<List<Item>> {
    override fun option(): Option<List<Item>> = option

    override fun formatValue(): Text = option().pendingValue().let {
        Text.translatable(
            when (it.size) {
                0 -> Texts.CONFIG_CONTROLLER_ITEMS_LIST_VALUE_EMPTY
                1 -> Texts.CONFIG_CONTROLLER_ITEMS_LIST_VALUE_SINGLE
                else -> Texts.CONFIG_CONTROLLER_ITEMS_LIST_VALUE_MULTIPLE
            }, it.size
        )
    }

    override fun provideWidget(screen: YACLScreen, widgetDimension: Dimension<Int>?): AbstractWidget =
        ItemsListControllerWidget(this, screen, widgetDimension)
}

private class ItemsListControllerWidget(
    controller: ItemsListController,
    screen: YACLScreen,
    widgetDimension: Dimension<Int>?
) : ControllerWidget<ItemsListController>(controller, screen, widgetDimension) {
    private val observableValue = ObservableValue(control.option().pendingValue().toPersistentList())

    init {
        control.option().addEventListener { option, _ ->
            observableValue.value = option.pendingValue().toPersistentList()
        }
        observableValue.addListener {
            controller.option().requestSet(it)
        }
    }

    override fun getHoveredControlWidth(): Int = 0

    private fun clicked() {
        val screen = ItemsListScreen(screen, control.option().name(), observableValue)
        client.setScreen(screen)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!isMouseOver(mouseX, mouseY) || !isAvailable) {
            return false
        }

        clicked()
        return true
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (!isFocused) {
            return false
        }

        if (keyCode == InputUtil.GLFW_KEY_ENTER || keyCode == InputUtil.GLFW_KEY_SPACE || keyCode == InputUtil.GLFW_KEY_KP_ENTER) {
            clicked()
            return true
        }

        return false
    }
}