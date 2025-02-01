package top.fifthlight.touchcontroller.layout

import top.fifthlight.touchcontroller.assets.Textures
import top.fifthlight.touchcontroller.control.PerspectiveSwitchButton
import top.fifthlight.touchcontroller.control.PerspectiveSwitchButtonStyle
import top.fifthlight.touchcontroller.gal.CameraPerspective

fun Context.PerspectiveSwitchButton(config: PerspectiveSwitchButton) {
    val (newPointer) = Button("perspective_switch") {
        val texture = when (config.style) {
            PerspectiveSwitchButtonStyle.CLASSIC -> when (input.perspective) {
                CameraPerspective.FIRST_PERSON -> Textures.GUI_PERSPECTIVE_PERSPECTIVE_FIRST_PERSON
                CameraPerspective.THIRD_PERSON_BACK -> Textures.GUI_PERSPECTIVE_PERSPECTIVE_THIRD_PERSON_BACK
                CameraPerspective.THIRD_PERSON_FRONT -> Textures.GUI_PERSPECTIVE_PERSPECTIVE_THIRD_PERSON_FRONT
            }

            PerspectiveSwitchButtonStyle.CLASSIC_SIMPLE -> Textures.GUI_PERSPECTIVE_PERSPECTIVE_SIMPLE

            PerspectiveSwitchButtonStyle.NEW -> when (input.perspective) {
                CameraPerspective.FIRST_PERSON -> Textures.GUI_PERSPECTIVE_PERSPECTIVE_FIRST_PERSON_NEW
                CameraPerspective.THIRD_PERSON_BACK -> Textures.GUI_PERSPECTIVE_PERSPECTIVE_THIRD_PERSON_BACK_NEW
                CameraPerspective.THIRD_PERSON_FRONT -> Textures.GUI_PERSPECTIVE_PERSPECTIVE_THIRD_PERSON_FRONT_NEW
            }

            PerspectiveSwitchButtonStyle.NEW_SIMPLE -> Textures.GUI_PERSPECTIVE_PERSPECTIVE_SIMPLE_NEW
        }
        Texture(texture)
    }
    if (newPointer) {
        result.nextPerspective = true
    }
}