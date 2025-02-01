package top.fifthlight.touchcontroller.gal

data class GameState(
    val inGame: Boolean,
    val inGui: Boolean,
    val perspective: CameraPerspective,
)

interface GameStateProvider {
    fun currentState(): GameState
}
