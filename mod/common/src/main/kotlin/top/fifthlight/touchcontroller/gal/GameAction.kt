package top.fifthlight.touchcontroller.gal

import top.fifthlight.combine.data.Text

interface GameAction {
    fun openChatScreen()
    fun openGameMenu()
    fun sendMessage(text: Text)
    fun nextPerspective()
    fun takeScreenshot()
    fun takePanorama() {}
}
