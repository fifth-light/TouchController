package top.fifthlight.touchcontroller.config

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.dsl.YetAnotherConfigLib
import dev.isxander.yacl3.dsl.slider
import dev.isxander.yacl3.dsl.textSwitch
import kotlinx.collections.immutable.toPersistentList
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.Item
import org.koin.core.context.GlobalContext
import top.fifthlight.touchcontroller.TouchController
import top.fifthlight.touchcontroller.asset.Texts
import top.fifthlight.touchcontroller.config.widget.ItemsListController
import top.fifthlight.touchcontroller.ext.ItemsList

fun openConfigScreen(parent: Screen): Screen {
    val context = GlobalContext.get()
    val configHolder: TouchControllerConfigHolder = context.get()
    var config = configHolder.config.value

    return YetAnotherConfigLib(TouchController.NAMESPACE) {
        title(Texts.OPTIONS_SCREEN_TITLE)

        val globalCategory by categories.registering("global") {
            name(Texts.OPTIONS_CATEGORY_GLOBAL_TITLE)
            tooltip(Texts.OPTIONS_CATEGORY_GLOBAL_TOOLTIP)

            val regularGroup by groups.registering("regular") {
                name(Texts.OPTIONS_CATEGORY_GLOBAL_REGULAR_TITLE)

                val disableMouseMove by options.registering {
                    name(Texts.OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_MOVE_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_MOVE_DESCRIPTION))
                    controller(textSwitch())
                    binding(true, { config.disableMouseMove }, { config = config.copy(disableMouseMove = it) })
                }

                val disableMouseClick by options.registering {
                    name(Texts.OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_CLICK_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_CLICK_DESCRIPTION))
                    controller(textSwitch())
                    binding(true, { config.disableMouseClick }, { config = config.copy(disableMouseClick = it) })
                }

                val disableMouseLock by options.registering {
                    name(Texts.OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_LOCK_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_MOUSE_LOCK_DESCRIPTION))
                    controller(textSwitch())
                    binding(false, { config.disableMouseLock }, { config = config.copy(disableMouseLock = it) })
                }

                val disableCrosshair by options.registering {
                    name(Texts.OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_CROSSHAIR_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_CROSSHAIR_DESCRIPTION))
                    controller(textSwitch())
                    binding(true, { config.disableCrosshair }, { config = config.copy(disableCrosshair = it) })
                }

                val disableHotBarKey by options.registering {
                    name(Texts.OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_HOT_BAR_KEY_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_GLOBAL_REGULAR_DISABLE_HOT_BAR_KEY_DESCRIPTION))
                    controller(textSwitch())
                    binding(false, { config.disableHotBarKey }, { config = config.copy(disableHotBarKey = it) })
                }

                val vibration by options.registering {
                    name(Texts.OPTIONS_CATEGORY_GLOBAL_REGULAR_VIBRATION_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_GLOBAL_REGULAR_VIBRATION_DESCRIPTION))
                    controller(textSwitch())
                    binding(true, { config.vibration }, { config = config.copy(vibration = it) })
                }

                val quickHandSwap by options.registering {
                    name(Texts.OPTIONS_CATEGORY_GLOBAL_REGULAR_QUICK_HAND_SWAP_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_GLOBAL_REGULAR_QUICK_HAND_SWAP_DESCRIPTION))
                    controller(textSwitch())
                    binding(false, { config.quickHandSwap }, { config = config.copy(quickHandSwap = it) })
                }
            }

            val controlGroup by groups.registering("control") {
                name(Texts.OPTIONS_CATEGORY_GLOBAL_CONTROL_TITLE)

                val viewMovementSensitivity by options.registering {
                    name(Texts.OPTIONS_CATEGORY_GLOBAL_CONTROL_VIEW_MOVEMENT_SENSITIVITY_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_GLOBAL_CONTROL_VIEW_MOVEMENT_SENSITIVITY_DESCRIPTION))
                    controller(slider(0f..900f))
                    binding(
                        495f,
                        { config.viewMovementSensitivity },
                        { config = config.copy(viewMovementSensitivity = it) }
                    )
                }

                val viewHoldDetectThreshold by options.registering {
                    name(Texts.OPTIONS_CATEGORY_GLOBAL_CONTROL_VIEW_HOLD_DETECT_THRESHOLD_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_GLOBAL_CONTROL_VIEW_HOLD_DETECT_THRESHOLD_DESCRIPTION))
                    controller(slider(0..10))
                    binding(
                        2,
                        { config.viewHoldDetectThreshold },
                        { config = config.copy(viewHoldDetectThreshold = it) }
                    )
                }
            }

            groups.register("debug", OptionGroup.createBuilder().apply {
                name(Texts.OPTIONS_CATEGORY_GLOBAL_DEBUG_TITLE)
                collapsed(true)

                option(Option.createBuilder<Boolean>().apply {
                    name(Texts.OPTIONS_CATEGORY_GLOBAL_DEBUG_SHOW_POINTERS_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_GLOBAL_DEBUG_SHOW_POINTERS_DESCRIPTION))
                    controller(textSwitch())
                    binding(false, { config.showPointers }, { config = config.copy(showPointers = it) })
                }.build())

                option(Option.createBuilder<Boolean>().apply {
                    name(Texts.OPTIONS_CATEGORY_GLOBAL_DEBUG_ENABLE_TOUCH_EMULATION_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_GLOBAL_DEBUG_ENABLE_TOUCH_EMULATION_DESCRIPTION))
                    controller(textSwitch())
                    binding(false, { config.enableTouchEmulation }, { config = config.copy(enableTouchEmulation = it) })
                }.build())
            }.build())
        }

        val itemsCategory by categories.registering("items") {
            name(Texts.OPTIONS_CATEGORY_ITEMS_TITLE)
            tooltip(Texts.OPTIONS_CATEGORY_ITEMS_TOOLTIP)

            val usableItemsGroup by groups.registering("usable_items") {
                name(Texts.OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_TITLE)

                val usableItems by options.registering<List<Item>> {
                    name(Texts.OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_ITEMS_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_ITEMS_DESCRIPTION))
                    customController { ItemsListController(it) }
                    binding(
                        defaultUsableItems,
                        { config.usableItems.items },
                        { config = config.copy(usableItems = ItemsList(it.toPersistentList())) }
                    )
                }

                val foodUsable by options.registering {
                    name(Texts.OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_FOOD_USABLE_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_FOOD_USABLE_DESCRIPTION))
                    controller(textSwitch())
                    binding(true, { config.foodUsable }, { config = config.copy(foodUsable = it) })
                }

                val projectileUsable by options.registering {
                    name(Texts.OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_PROJECTILE_USABLE_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_PROJECTILE_USABLE_DESCRIPTION))
                    controller(textSwitch())
                    binding(true, { config.projectileUsable }, { config = config.copy(projectileUsable = it) })
                }

                val rangedWeaponUsable by options.registering {
                    name(Texts.OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_RANGED_WEAPONS_USABLE_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_RANGED_WEAPONS_USABLE_DESCRIPTION))
                    controller(textSwitch())
                    binding(true, { config.rangedWeaponUsable }, { config = config.copy(rangedWeaponUsable = it) })
                }

                val equippableUsable by options.registering {
                    name(Texts.OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_EQUIPPABLE_ITEMS_USABLE_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_EQUIPPABLE_ITEMS_USABLE_DESCRIPTION))
                    controller(textSwitch())
                    binding(true, { config.equippableUsable }, { config = config.copy(equippableUsable = it) })
                }

                val bundleUsable by options.registering {
                    name(Texts.OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_BUNDLE_USABLE_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_BUNDLE_USABLE_DESCRIPTION))
                    controller(textSwitch())
                    binding(true, { config.bundleUsable }, { config = config.copy(bundleUsable = it) })
                }

                val consumableUsable by options.registering {
                    name(Texts.OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_CONSUMABLE_USABLE_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_ITEMS_USABLE_ITEMS_CONSUMABLE_USABLE_DESCRIPTION))
                    controller(textSwitch())
                    binding(true, { config.consumableUsable }, { config = config.copy(consumableUsable = it) })
                }
            }


            val showCrosshairItemsGroup by groups.registering("show_crosshair_items") {
                name(Texts.OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_ITEMS_TITLE)

                val showCrosshairItems by options.registering<List<Item>> {
                    name(Texts.OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_ITEMS_DESCRIPTION))
                    customController { ItemsListController(it) }
                    binding(
                        defaultShowCrosshairItems,
                        { config.showCrosshairItems.items },
                        { config = config.copy(showCrosshairItems = ItemsList(it.toPersistentList())) }
                    )
                }

                val projectileShowCrosshair by options.registering {
                    name(Texts.OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_PROJECTILE_SHOW_CROSSHAIR_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_PROJECTILE_SHOW_CROSSHAIR_DESCRIPTION))
                    controller(textSwitch())
                    binding(
                        true,
                        { config.projectileShowCrosshair },
                        { config = config.copy(projectileShowCrosshair = it) }
                    )
                }

                val rangedWeaponShowCrosshair by options.registering {
                    name(Texts.OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_RANGED_WEAPONS_SHOW_CROSSHAIR_TITLE)
                    description(OptionDescription.of(Texts.OPTIONS_CATEGORY_ITEMS_SHOW_CROSSHAIR_ITEMS_RANGED_WEAPONS_SHOW_CROSSHAIR_DESCRIPTION))
                    controller(textSwitch())
                    binding(
                        true,
                        { config.rangedWeaponShowCrosshair },
                        { config = config.copy(rangedWeaponShowCrosshair = it) }
                    )
                }
            }
        }

        categories.register(
            "custom", CustomCategory(
                name = Texts.OPTIONS_CATEGORY_CUSTOM_TITLE,
                tooltip = Texts.OPTIONS_CATEGORY_CUSTOM_TOOLTIP,
            )
        )

        save {
            configHolder.saveConfig(config)
        }
    }.generateScreen(parent)
}