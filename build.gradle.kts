plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm).apply(false)
    alias(libs.plugins.jetbrains.kotlin.serialization).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.rust.android.gradle).apply(false)
    alias(libs.plugins.jetbrains.kotlin.android).apply(false)
    alias(libs.plugins.maven.publish).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
}

subprojects {
    group = "top.fifthlight.touchcontroller"

    repositories {
        maven {
            name = "Terraformers"
            url = uri("https://maven.terraformersmc.com/")
        }
        maven {
            name = "Xander Maven"
            url = uri("https://maven.isxander.dev/releases")
        }
        mavenCentral()
        google()
    }
}