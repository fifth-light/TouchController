package top.fifthlight.touchcontroller.ui.screen

import top.fifthlight.combine.data.TextFactory
import top.fifthlight.combine.screen.ScreenFactory
import top.fifthlight.touchcontroller.ui.model.LicenseScreenViewModel
import top.fifthlight.touchcontroller.ui.view.LicenseScreen

fun openLicenseScreen(
    screenFactory: ScreenFactory,
    textFactory: TextFactory,
    licenseText: String,
) {
    screenFactory.openScreen(
        title = textFactory.empty(),
        viewModelFactory = { scope, _ ->
            LicenseScreenViewModel(scope, licenseText)
        },
        content = {
            LicenseScreen(it)
        }
    )
}