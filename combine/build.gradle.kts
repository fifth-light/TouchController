plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.compose.compiler)
}

version = "0.0.1"

kotlin {
    jvmToolchain(8)
}

dependencies {
    api(libs.kotlinx.collections.immutable)
    api(libs.compose.runtime)
    api(project(":common-data"))
}
