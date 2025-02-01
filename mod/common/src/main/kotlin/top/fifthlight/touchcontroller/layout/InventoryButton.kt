package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.gal.KeyBindingType

fun Context.InventoryButton() {
    val (_, _, release) = Button(id = "inventory") { clicked ->
        if (clicked) {
            Texture(texture = Textures.GUI_INVENTORY_INVENTORY_ACTIVE)
        } else {
            Texture(texture = Textures.GUI_INVENTORY_INVENTORY)
        }
    }
    if (release) {
        keyBindingHandler.getState(KeyBindingType.INVENTORY).clicked = true
    }
}