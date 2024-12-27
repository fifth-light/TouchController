package top.fifthlight.touchcontroller.layout

enum class CrosshairTarget {
    BLOCK,
    ENTITY,
    MISS
}

interface ViewActionProvider {
    fun getCrosshairTarget(): CrosshairTarget?
    fun getCurrentBreakingProgress(): Float
}