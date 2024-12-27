package top.fifthlight.combine.data

import androidx.compose.runtime.Immutable

@Immutable
sealed class Identifier {
    data class Namespaced(val namespace: String, val id: String): Identifier() {
        override fun toString() = "$namespace:$id"
    }

    data class Vanilla(val id: String): Identifier() {
        override fun toString() = "minecraft:$id"
    }

    companion object {
        fun of(namespace: String, id: String) = Namespaced(namespace, id)
        fun ofVanilla(id: String) = Vanilla(id)
    }
}

fun Identifier(string: String): Identifier {
    val colonIndex = string.indexOf(':')
    return if (colonIndex == -1) {
        Identifier.ofVanilla(string)
    } else {
        val namespace = string.substring(0, colonIndex)
        val id = string.substring(colonIndex + 1)
        Identifier.of(namespace, id)
    }
}