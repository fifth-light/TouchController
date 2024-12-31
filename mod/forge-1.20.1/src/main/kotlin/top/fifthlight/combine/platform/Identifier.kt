package top.fifthlight.combine.platform

import net.minecraft.resources.ResourceLocation
import top.fifthlight.combine.data.Identifier as CombineIdentifier

fun CombineIdentifier.toMinecraft(): ResourceLocation = when (this) {
    is CombineIdentifier.Vanilla -> ResourceLocation(ResourceLocation.DEFAULT_NAMESPACE, id)
    is CombineIdentifier.Namespaced -> ResourceLocation(namespace, id)
}

fun ResourceLocation.toCombine() = if (this.namespace == ResourceLocation.DEFAULT_NAMESPACE) {
    CombineIdentifier.Vanilla(path)
} else {
    CombineIdentifier.Namespaced(namespace, path)
}