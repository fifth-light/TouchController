package top.fifthlight.touchcontroller.asset

import net.minecraft.text.MutableText
import net.minecraft.text.Text
import top.fifthlight.touchcontroller.TouchController

// @formatter:off
object Texts {
    private fun key(id: String) = "${TouchController.NAMESPACE}.$id"
    private fun translatable(id: String): MutableText = Text.translatable(key(id))

    // TODO generate binding below from translation JSON file

    val WARNING_PROXY_NOT_CONNECTED = translatable("warning.proxy_not_connected")

    val OPTIONS_SCREEN = translatable("screen.options")
    val OPTIONS_SCREEN_TITLE = translatable("screen.options.title")

    val OPTIONS_CATEGORY_GLOBAL_TITLE = translatable("screen.options.category.global.title")
    val OPTIONS_CATEGORY_GLOBAL_TOOLTIP = translatable("screen.options.category.global.tooltip")

    val OPTIONS_CATEGORY_GLOBAL_REGULAR_TITLE = translatable("screen.options.category.global.regular.title")
    val OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_MOVE_TITLE = translatable("screen.options.category.global.regular.disable_mouse_move.title")
    val OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_MOVE_DESCRIPTION = translatable("screen.options.category.global.regular.disable_mouse_move.description")
    val OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_CLICK_TITLE = translatable("screen.options.category.global.regular.disable_mouse_click.title")
    val OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_CLICK_DESCRIPTION = translatable("screen.options.category.global.regular.disable_mouse_click.description")
    val OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_LOCK_TITLE = translatable("screen.options.category.global.regular.disable_cursor_lock.title")
    val OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_LOCK_DESCRIPTION = translatable("screen.options.category.global.regular.disable_cursor_lock.description")
    val OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_CROSSHAIR_TITLE = translatable("screen.options.category.global.regular.disable_crosshair.title")
    val OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_CROSSHAIR_DESCRIPTION = translatable("screen.options.category.global.regular.disable_crosshair.description")

    val OPTIONS_CATEGORY_GLOBAL_CONTROL_TITLE = translatable("screen.options.category.control.title")
    val OPTIONS_CATEGORY_GLOBAL_CONTROL_VIEW_MOVEMENT_SENSITIVITY_TITLE = translatable("screen.options.category.control.view_movement_sensitivity.title")
    val OPTIONS_CATEGORY_GLOBAL_CONTROL_VIEW_MOVEMENT_SENSITIVITY_DESCRIPTION = translatable("screen.options.category.control.view_movement_sensitivity.description")
    val OPTIONS_CATEGORY_GLOBAL_CONTROL_VIEW_HOLD_DETECT_THRESHOLD_TITLE = translatable("screen.options.category.control.view_hold_detect_threshold.title")
    val OPTIONS_CATEGORY_GLOBAL_CONTROL_VIEW_HOLD_DETECT_THRESHOLD_DESCRIPTION = translatable("screen.options.category.control.view_hold_detect_threshold.description")

    val OPTIONS_CATEGORY_GLOBAL_DEBUG_TITLE = translatable("screen.options.category.global.debug.title")
    val OPTIONS_CATEGORY_GLOBAL_DEBUG_SHOW_POINTERS_TITLE = translatable("screen.options.category.global.debug.show_pointers.title")
    val OPTIONS_CATEGORY_GLOBAL_DEBUG_SHOW_POINTERS_DESCRIPTION = translatable("screen.options.category.global.debug.show_pointers.description")
    val OPTIONS_CATEGORY_GLOBAL_DEBUG_ENABLE_TOUCH_EMULATION_TITLE = translatable("screen.options.category.global.debug.enable_touch_emulation.title")
    val OPTIONS_CATEGORY_GLOBAL_DEBUG_ENABLE_TOUCH_EMULATION_DESCRIPTION = translatable("screen.options.category.global.debug.enable_touch_emulation.description")

    val OPTIONS_CATEGORY_CUSTOM_TITLE = translatable("screen.options.category.custom.title")
    val OPTIONS_CATEGORY_CUSTOM_TOOLTIP = translatable("screen.options.category.custom.tooltip")
    
    val OPTIONS_SAVE_TITLE = translatable("screen.options.save.title")
    val OPTIONS_SAVE_TOOLTIP = translatable("screen.options.save.tooltip")
    val OPTIONS_FINISH_TITLE = translatable("screen.options.finish.title")
    val OPTIONS_FINISH_TOOLTIP = translatable("screen.options.finish.tooltip")
    val OPTIONS_RESET_TITLE = translatable("screen.options.reset.title")
    val OPTIONS_RESET_TOOLTIP = translatable("screen.options.reset.tooltip")
    val OPTIONS_UNDO_TITLE = translatable("screen.options.undo.title")
    val OPTIONS_UNDO_TOOLTIP = translatable("screen.options.undo.tooltip")
    val OPTIONS_CANCEL_TITLE = translatable("screen.options.cancel.title")
    val OPTIONS_CANCEL_TOOLTIP = translatable("screen.options.cancel.tooltip")
    val OPTIONS_SAVE_BEFORE_CLOSING_TITLE = translatable("screen.options.save_before_closing.title")
    val OPTIONS_REMOVE_TITLE = translatable("screen.options.remove.title")
    val OPTIONS_REMOVE_TOOLTIP = translatable("screen.options.remove.tooltip")

    val OPTIONS_WIDGET_GENERAL_PROPERTY_ALIGN_TOP_LEFT = translatable("screen.options.widget.general.property.top.left")
    val OPTIONS_WIDGET_GENERAL_PROPERTY_ALIGN_TOP_CENTER = translatable("screen.options.widget.general.property.top.center")
    val OPTIONS_WIDGET_GENERAL_PROPERTY_ALIGN_TOP_RIGHT = translatable("screen.options.widget.general.property.top.right")
    val OPTIONS_WIDGET_GENERAL_PROPERTY_ALIGN_CENTER_LEFT = translatable("screen.options.widget.general.property.center.left")
    val OPTIONS_WIDGET_GENERAL_PROPERTY_ALIGN_CENTER_CENTER = translatable("screen.options.widget.general.property.center.center")
    val OPTIONS_WIDGET_GENERAL_PROPERTY_ALIGN_CENTER_RIGHT = translatable("screen.options.widget.general.property.center.right")
    val OPTIONS_WIDGET_GENERAL_PROPERTY_ALIGN_BOTTOM_LEFT = translatable("screen.options.widget.general.property.bottom.left")
    val OPTIONS_WIDGET_GENERAL_PROPERTY_ALIGN_BOTTOM_CENTER = translatable("screen.options.widget.general.property.bottom.center")
    val OPTIONS_WIDGET_GENERAL_PROPERTY_ALIGN_BOTTOM_RIGHT = translatable("screen.options.widget.general.property.bottom.right")
    val OPTIONS_WIDGET_GENERAL_PROPERTY_OPACITY = key("screen.options.widget.general.property.opacity")

    val OPTIONS_WIDGET_DPAD_PROPERTY_CLASSIC = translatable("screen.options.widget.dpad.property.classic")
    val OPTIONS_WIDGET_DPAD_PROPERTY_SIZE = key("screen.options.widget.dpad.property.size")
    val OPTIONS_WIDGET_DPAD_PROPERTY_PADDING = key("screen.options.widget.dpad.property.padding")
    val OPTIONS_WIDGET_DPAD_PROPERTY_EXTRA_BUTTON_NONE = translatable("screen.options.widget.dpad.property.extra_button.none")
    val OPTIONS_WIDGET_DPAD_PROPERTY_EXTRA_BUTTON_JUMP = translatable("screen.options.widget.dpad.property.extra_button.jump")
    val OPTIONS_WIDGET_DPAD_PROPERTY_EXTRA_BUTTON_SNEAK = translatable("screen.options.widget.dpad.property.extra_button.sneak")

    val OPTIONS_WIDGET_JOYSTICK_SIZE = key("screen.options.widget.joystick.property.size")
    val OPTIONS_WIDGET_JOYSTICK_STICK_SIZE = key("screen.options.widget.joystick.property.stick_size")

    val OPTIONS_WIDGET_SNEAK_BUTTON_PROPERTY_SIZE = key("screen.options.widget.sneak_button.property.size")
    val OPTIONS_WIDGET_SNEAK_BUTTON_PROPERTY_CLASSIC = translatable("screen.options.widget.sneak_button.property.classic")

    val OPTIONS_WIDGET_JUMP_BUTTON_PROPERTY_SIZE = key("screen.options.widget.jump_button.property.size")
    val OPTIONS_WIDGET_JUMP_BUTTON_PROPERTY_CLASSIC = translatable("screen.options.widget.jump_button.property.classic")

    val OPTIONS_WIDGET_PAUSE_BUTTON_PROPERTY_SIZE = key("screen.options.widget.pause_button.property.size")
    val OPTIONS_WIDGET_PAUSE_BUTTON_PROPERTY_CLASSIC = translatable("screen.options.widget.pause_button.property.classic")

    val OPTIONS_WIDGET_CHAT_BUTTON_PROPERTY_SIZE = key("screen.options.widget.chat_button.property.size")
    val OPTIONS_WIDGET_CHAT_BUTTON_PROPERTY_CLASSIC = translatable("screen.options.widget.chat_button.property.classic")

    val OPTIONS_WIDGET_ASCEND_BUTTON_PROPERTY_SIZE = key("screen.options.widget.ascend_button.property.size")
    val OPTIONS_WIDGET_ASCEND_BUTTON_PROPERTY_CLASSIC = translatable("screen.options.widget.ascend_button.property.classic")

    val OPTIONS_WIDGET_DESCEND_BUTTON_PROPERTY_SIZE = key("screen.options.widget.descend_button.property.size")
    val OPTIONS_WIDGET_DESCEND_BUTTON_PROPERTY_CLASSIC = translatable("screen.options.widget.descend_button.property.classic")

    val CONFIG_CONTROLLER_ITEMS_LIST_VALUE_EMPTY = key("screen.config.controller.items.value.empty")
    val CONFIG_CONTROLLER_ITEMS_LIST_VALUE_SINGLE = key("screen.config.controller.items.value.single")
    val CONFIG_CONTROLLER_ITEMS_LIST_VALUE_MULTIPLE = key("screen.config.controller.items.value.multiple")

    val OPTIONS_CATEGORY_ITEMS_TITLE = translatable("screen.options.category.items.title")
    val OPTIONS_CATEGORY_ITEMS_TOOLTIP = translatable("screen.options.category.items.tooltip")

    val OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_TITLE = translatable("screen.options.category.items.usable_items.title")
    val OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_ITEMS_TITLE = translatable("screen.options.category.items.usable_items.items.title")
    val OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_ITEMS_DESCRIPTION = translatable("screen.options.category.items.usable_items.items.description")
    val OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_FOOD_USABLE_TITLE = translatable("screen.options.category.items.usable_items.food_usable.title")
    val OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_FOOD_USABLE_DESCRIPTION = translatable("screen.options.category.items.usable_items.food_usable.description")
    val OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_PROJECTILE_USABLE_TITLE = translatable("screen.options.category.items.usable_items.projectile_usable.title")
    val OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_PROJECTILE_USABLE_DESCRIPTION = translatable("screen.options.category.items.usable_items.projectile_usable.description")
    val OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_RANGED_WEAPONS_USABLE_TITLE = translatable("screen.options.category.items.usable_items.ranged_weapons_usable.title")
    val OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_RANGED_WEAPONS_USABLE_DESCRIPTION = translatable("screen.options.category.items.usable_items.ranged_weapons_usable.description")
    val OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_EQUIPPABLE_ITEMS_USABLE_TITLE = translatable("screen.options.category.items.usable_items.equippable_items_usable.title")
    val OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_EQUIPPABLE_ITEMS_USABLE_DESCRIPTION = translatable("screen.options.category.items.usable_items.equippable_items_usable.description")
    val OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_BUNDLE_USABLE_TITLE = translatable("screen.options.category.items.usable_items.bundle_usable.title")
    val OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_BUNDLE_USABLE_DESCRIPTION = translatable("screen.options.category.items.usable_items.bundle_usable.description")
    val OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_CONSUMABLE_USABLE_TITLE = translatable("screen.options.category.items.usable_items.consumable_usable.title")
    val OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_CONSUMABLE_USABLE_DESCRIPTION = translatable("screen.options.category.items.usable_items.consumable_usable.description")

    val OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_TITLE = translatable("screen.options.category.items.show_crosshair_items.title")
    val OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_ITEMS_TITLE = translatable("screen.options.category.items.show_crosshair_items.items.title")
    val OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_ITEMS_DESCRIPTION = translatable("screen.options.category.items.show_crosshair_items.items.description")
    val OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_PROJECTILE_SHOW_CROSSHAIR_TITLE = translatable("screen.options.category.items.show_crosshair_items.projectile_show_crosshair.title")
    val OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_PROJECTILE_SHOW_CROSSHAIR_DESCRIPTION = translatable("screen.options.category.items.show_crosshair_items.projectile_show_crosshair.description")
    val OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_RANGED_WEAPONS_SHOW_CROSSHAIR_TITLE = translatable("screen.options.category.items.show_crosshair_items.ranged_weapons_show_crosshair.title")
    val OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_RANGED_WEAPONS_SHOW_CROSSHAIR_DESCRIPTION = translatable("screen.options.category.items.show_crosshair_items.ranged_weapons_show_crosshair.description")

    val ITEMS_SCREEN_ITEMS_LIST_MESSAGE = translatable("screen.items.items_list.message")

    val ITEMS_DONE_TITLE = translatable("screen.items.done.title")
    val ITEMS_DONE_TOOLTIP = translatable("screen.items.done.tooltip")
    val ITEMS_REMOVE_TITLE = translatable("screen.items.remove.title")
    val ITEMS_REMOVE_TOOLTIP = translatable("screen.items.remove.tooltip")
}