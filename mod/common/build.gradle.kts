plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
}

val modVersion: String by extra.properties

version = modVersion
group = "top.fifthlight.touchcontroller"

sourceSets.main {
    kotlin.srcDir(project(":mod:resources").layout.buildDirectory.dir("generated/kotlin/resources"))
    kotlin.srcDir(project(":mod:resources").layout.buildDirectory.dir("generated/kotlin/buildinfo"))
}

tasks.compileKotlin {
    dependsOn(":mod:resources:generate")
}

dependencies {
    api(project(":common-data"))
    implementation(project(":proxy-client"))
    implementation(project(":proxy-server"))

    api(libs.compose.runtime)
    api(project(":combine"))

    api(libs.koin.core)
    api(libs.koin.compose)
    api(libs.koin.logger.slf4j)
    api(libs.kotlinx.collections.immutable)
    api(libs.kotlinx.serialization.json)
    api(libs.kotlinx.coroutines.core)

    implementation(libs.aboutlibraries.core)
}

kotlin {
    jvmToolchain(8)
}
