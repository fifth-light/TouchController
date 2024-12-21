package top.fifthlight.combine.platform

import top.fifthlight.combine.data.Identifier

fun Identifier.toMinecraft(): net.minecraft.util.Identifier = when (this) {
    is Identifier.Vanilla -> net.minecraft.util.Identifier.ofVanilla(id)
    is Identifier.Namespaced -> net.minecraft.util.Identifier.of(namespace, id)
}