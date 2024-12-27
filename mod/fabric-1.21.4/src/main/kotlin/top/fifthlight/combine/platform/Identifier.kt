package top.fifthlight.combine.platform

import net.minecraft.util.Identifier
import top.fifthlight.combine.data.Identifier as CombineIdentifier

fun CombineIdentifier.toMinecraft(): Identifier = when (this) {
    is CombineIdentifier.Vanilla -> Identifier.ofVanilla(id)
    is CombineIdentifier.Namespaced -> Identifier.of(namespace, id)
}

fun Identifier.toCombine() = if (this.namespace == "minecraft") {
    CombineIdentifier.Vanilla(path)
} else {
    CombineIdentifier.Namespaced(namespace, path)
}