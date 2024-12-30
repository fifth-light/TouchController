package top.fifthlight.combine.platform

import net.minecraft.util.Identifier
import top.fifthlight.combine.data.Identifier as CombineIdentifier

fun CombineIdentifier.toMinecraft(): Identifier = when (this) {
    is CombineIdentifier.Vanilla -> Identifier(Identifier.DEFAULT_NAMESPACE, id)
    is CombineIdentifier.Namespaced -> Identifier(namespace, id)
}

fun Identifier.toCombine() = if (this.namespace == Identifier.DEFAULT_NAMESPACE) {
    CombineIdentifier.Vanilla(path)
} else {
    CombineIdentifier.Namespaced(namespace, path)
}