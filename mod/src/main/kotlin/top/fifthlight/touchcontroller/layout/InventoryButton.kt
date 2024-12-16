package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.asset.Textures

fun Context.InventoryButton() {
    val (_, _, release) = Button(id = "inventory") { clicked ->
        if (clicked) {
            Texture(id = Textures.INVENTORY_ACTIVE)
        } else {
            Texture(id = Textures.INVENTORY)
        }
    }
    if (release) {
        status.openInventory.click()
    }
}