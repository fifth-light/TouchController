package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.PanoramaButton

fun Context.PanoramaButton(config: PanoramaButton) {
    val (newClick) = Button(id = "panorama") {
        if (config.classic) {
            Texture(texture = Textures.GUI_PANORAMA_PANORAMA)
        } else {
            Texture(texture = Textures.GUI_PANORAMA_PANORAMA_NEW)
        }
    }

    if (newClick) {
        result.takePanorama = true
    }
}