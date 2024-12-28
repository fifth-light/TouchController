package top.fifthlight.touchcontroller.gal

data class GameState(
    val inGame: Boolean,
    val inGui: Boolean,
)

interface GameStateProvider {
    fun currentState(): GameState
}
