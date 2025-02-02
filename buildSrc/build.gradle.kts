plugins {
    `kotlin-dsl`
}

repositories {
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
    maven {
        name = "Forge"
        url = uri("https://maven.minecraftforge.net/")
    }
    maven {
        name = "Parchment"
        url = uri("https://maven.parchmentmc.org")
    }
    gradlePluginPortal()
}

dependencies {
    implementation(libs.modrinth.minotaur)
    implementation(libs.fabric.loom)
    implementation(libs.gr8)
    implementation(libs.forge.gradle)
    implementation(libs.parchmentmc.librarian.forgegradle)
    implementation(libs.aboutlibraries.plugin)
    implementation(libs.mixin)

    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
