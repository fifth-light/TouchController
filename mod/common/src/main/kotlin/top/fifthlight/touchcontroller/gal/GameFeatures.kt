package top.fifthlight.touchcontroller.gal

data class EntityFeatures(
    val haveLlama: Boolean,
    val haveStrider: Boolean,
)

data class GameFeatures(
    val dualWield: Boolean,
    val entity: EntityFeatures
)