plugins {
    alias(libs.plugins.fabric.loom).apply(false)
    alias(libs.plugins.jetbrains.kotlin.jvm).apply(false)
    alias(libs.plugins.jetbrains.kotlin.serialization).apply(false)
    alias(libs.plugins.modrinth.minotaur).apply(false)
    alias(libs.plugins.maven.publish).apply(false)
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
    }
}