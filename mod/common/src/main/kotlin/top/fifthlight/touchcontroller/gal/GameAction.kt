package top.fifthlight.touchcontroller.gal

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import top.fifthlight.combine.data.Text
import top.fifthlight.combine.data.TextFactory
import top.fifthlight.touchcontroller.assets.Texts

interface GameAction : KoinComponent {
    fun openChatScreen()
    fun openGameMenu()
    fun sendMessage(text: Text)
    fun nextPerspective()
    fun takeScreenshot()
    fun takePanorama() {
        val textFactory: TextFactory = get()
        sendMessage(textFactory.of(Texts.WARNING_TAKE_PANORAMA_UNSUPPORTED))
    }
}
