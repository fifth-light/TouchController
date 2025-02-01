package top.fifthlight.touchcontroller.gal

data class EntityFeatures(
    val haveCamel: Boolean,
    val haveLlama: Boolean,
    val haveStrider: Boolean,
)

data class GameFeatures(
    val dualWield: Boolean,
    val takePanorama: Boolean,
    val entity: EntityFeatures
)