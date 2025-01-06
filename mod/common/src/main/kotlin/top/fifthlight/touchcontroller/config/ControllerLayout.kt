package top.fifthlight.touchcontroller.config

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable
import top.fifthlight.touchcontroller.control.ControllerWidget
import top.fifthlight.touchcontroller.ext.ControllerLayoutSerializer
import top.fifthlight.touchcontroller.ext.LayerConditionSerializer
import top.fifthlight.touchcontroller.ext.LayoutLayerSerializer

enum class LayerConditionValue {
    NEVER,
    WANT,
    REQUIRE,
}

enum class LayerConditionKey {
    SWIMMING,
    FLYING,
    SNEAKING,
    SPRINTING,
    ON_GROUND,
    NOT_ON_GROUND,
    USING_ITEM,
    ON_MINECART,
    ON_BOAT,
    ON_PIG,
    ON_HORSE,
    ON_DONKEY,
    ON_LLAMA,
    ON_STRIDER,
    RIDING,
}

@Serializable(with = LayerConditionSerializer::class)
@JvmInline
value class LayoutLayerCondition(
    val conditions: PersistentMap<LayerConditionKey, LayerConditionValue> = persistentMapOf()
) {
    fun check(currentState: PersistentMap<LayerConditionKey, Boolean>): Boolean {
        var haveWant = false
        var haveFulfilledWant = false
        for (condition in conditions) {
            val current = currentState[condition.key]
            when (condition.value) {
                LayerConditionValue.NEVER -> if (current == true) {
                    return false
                }

                LayerConditionValue.WANT -> {
                    haveWant = true
                    if (current == true) {
                        haveFulfilledWant = true
                    }
                }

                LayerConditionValue.REQUIRE -> if (current != true) {
                    return false
                }
            }
        }
        if (haveWant && !haveFulfilledWant) {
            return false
        }
        return true
    }

    operator fun get(key: LayerConditionKey): LayerConditionValue? = conditions[key]
    fun set(key: LayerConditionKey, value: LayerConditionValue?) = LayoutLayerCondition(
        if (value == null) {
            conditions.remove(key)
        } else {
            conditions.put(key, value)
        }
    )
}

fun layoutLayerConditionOf(vararg pairs: Pair<LayerConditionKey, LayerConditionValue>) =
    LayoutLayerCondition(persistentMapOf(*pairs))

const val DEFAULT_LAYER_NAME = "Unnamed layer"

@Serializable(with = LayoutLayerSerializer::class)
data class LayoutLayer(
    val name: String = DEFAULT_LAYER_NAME,
    val widgets: PersistentList<ControllerWidget> = persistentListOf(),
    val condition: LayoutLayerCondition = LayoutLayerCondition(),
)

@JvmInline
@Serializable(with = ControllerLayoutSerializer::class)
value class ControllerLayout(
    val layers: PersistentList<LayoutLayer> = persistentListOf(),
)

fun controllerLayoutOf(vararg layers: LayoutLayer) = ControllerLayout(persistentListOf(*layers))
