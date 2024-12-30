import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

version = "0.0.1"

dependencies {
    implementation(project(":proxy-client"))
    implementation(libs.kotlinx.coroutines.core)
}

kotlin {
    jvmToolchain(8)

    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_1_8)
    }
}
