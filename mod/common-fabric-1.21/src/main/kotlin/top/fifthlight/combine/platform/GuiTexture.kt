package top.fifthlight.combine.platform

import net.minecraft.util.Identifier
import top.fifthlight.combine.paint.GuiTexture

fun GuiTexture.toIdentifier(): Identifier = when (this) {
    GuiTexture.BUTTON -> Identifier.ofVanilla("widget/button")
    GuiTexture.BUTTON_HOVER -> Identifier.ofVanilla("widget/button_highlighted")
    GuiTexture.BUTTON_ACTIVE -> Identifier.ofVanilla("widget/button_highlighted")
    GuiTexture.BUTTON_DISABLED -> Identifier.ofVanilla("widget/button_disabled")
}