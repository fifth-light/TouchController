plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.compose.compiler)
}

version = "0.0.1"

dependencies {
    api(libs.kotlinx.collections.immutable)
    api(libs.compose.runtime)
    api(project(":common-data"))
}

kotlin {
    jvmToolchain(8)
}
