package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.ScreenshotButton

fun Context.ScreenshotButton(config: ScreenshotButton) {
    val (newClick) = Button(id = "screenshot") {
        if (config.classic) {
            Texture(texture = Textures.GUI_SCREENSHOT_SCREENSHOT)
        } else {
            Texture(texture = Textures.GUI_SCREENSHOT_SCREENSHOT_NEW)
        }
    }

    if (newClick) {
        result.takeScreenshot = true
    }
}