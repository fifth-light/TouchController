plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    id("TouchController.toolchain-conventions")
    id("TouchController.forge-conventions")
    id("TouchController.modrinth-conventions")
}

sourceSets.main {
    kotlin.srcDir("../common-forge/src/main/kotlin")
}