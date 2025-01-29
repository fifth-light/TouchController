plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    id("TouchController.toolchain-conventions")
    id("TouchController.fabric-conventions")
    id("TouchController.modrinth-conventions")
}

sourceSets.main {
    kotlin.srcDir("../common-fabric/src/main/kotlin")
    kotlin.srcDir("../common-fabric-1.21/src/main/kotlin")
    java.srcDir("../common-fabric-1.21/src/main/java")
}
