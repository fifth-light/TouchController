package top.fifthlight.combine.platform

import net.minecraft.util.ResourceLocation
import top.fifthlight.combine.data.Identifier as CombineIdentifier

fun CombineIdentifier.toMinecraft(): ResourceLocation = when (this) {
    is CombineIdentifier.Vanilla -> ResourceLocation("minecraft", id)
    is CombineIdentifier.Namespaced -> ResourceLocation(namespace, id)
}

fun ResourceLocation.toCombine() = if (this.namespace == "minecraft") {
    CombineIdentifier.Vanilla(path)
} else {
    CombineIdentifier.Namespaced(namespace, path)
}