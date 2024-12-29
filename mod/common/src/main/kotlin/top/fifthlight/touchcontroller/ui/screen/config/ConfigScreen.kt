package top.fifthlight.touchcontroller.ui.screen.config

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.combine.screen.ScreenFactory
import top.fifthlight.touchcontroller.assets.Texts
import top.fifthlight.touchcontroller.ui.model.ConfigScreenViewModel
import top.fifthlight.touchcontroller.ui.view.config.ConfigScreen

object ConfigScreenGetter : KoinComponent {
    private val textFactory: TextFactory by inject()

    fun getText() = textFactory.toNative(textFactory.of(Texts.SCREEN_OPTIONS))
    fun getScreen(parent: Any?): Any = getConfigScreen(parent)
}

fun KoinComponent.getConfigScreen(parent: Any?): Any {
    val screenFactory: ScreenFactory = get()
    val textFactory: TextFactory = get()

    return screenFactory.getScreen(
        parent = parent,
        title = textFactory.empty(),
        viewModelFactory = { scope, closeHandler ->
            ConfigScreenViewModel(scope, closeHandler)
        },
        onDismissRequest = { viewModel ->
            viewModel.tryExit()
            true
        },
        content = {
            ConfigScreen(it)
        }
    )
}
