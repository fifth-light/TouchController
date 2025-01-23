plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

version = "0.0.1"

dependencies {
    implementation(libs.kotlinx.serialization.core)
    api(libs.kotlinx.collections.immutable)
    api(libs.compose.runtime)
    api(project(":common-data"))
}

kotlin {
    jvmToolchain(8)
}
