import net.minecraftforge.gradle.userdev.tasks.RenameJarInPlace
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    idea
    alias(libs.plugins.forge.gradle)
    alias(libs.plugins.parchmentmc.librarian.forgegradle)
    alias(libs.plugins.mixin)
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.shadow)
}

version = "0.0.13"
group = "top.fifthlight.touchcontroller"

val modName = "TouchController"
val modId = "touchcontroller"
base {
    archivesName = modName
}

minecraft {
    mappings("parchment", "2023.09.03-1.20.1")

    runs {
        copyIdeResources = true

        configureEach {
            workingDirectory(project.file("run"))
            properties["forge.logging.markers"] = "REGISTRIES"
            properties["forge.logging.console.level"] = "debug"

            mods {
                create(modId) {
                    sources(sourceSets.main.get())
                }
            }
        }

        create("client") {
            workingDirectory(project.file("run"))
        }
    }
}

mixin {
    add(sourceSets.getByName("main"), "mixins.${modId}.refmap.json")
    config("${modId}.mixins.json")
}

configurations.shadow {
    isTransitive = false
}

fun DependencyHandlerScope.shadeAndImplementation(dependency: Any) {
    shadow(dependency)
    implementation(dependency)
}

dependencies {
    minecraft("net.minecraftforge:forge:1.20.1-47.3.0")
    implementation("thedarkcolour:kotlinforforge:4.11.0")

    shadeAndImplementation(project(":common-data"))
    shadeAndImplementation(project(":mod:common"))

    shadeAndImplementation(project(":proxy-windows"))
    shadeAndImplementation(project(":proxy-server-android"))
    shadeAndImplementation(project(":proxy-client"))
    shadeAndImplementation(project(":proxy-server"))

    shadeAndImplementation(libs.androidx.collection)
    shadeAndImplementation(libs.compose.runtime)
    shadeAndImplementation(project(":combine"))

    shadeAndImplementation(libs.koin.core)
    shadeAndImplementation(libs.koin.compose)
    shadeAndImplementation(libs.koin.logger.slf4j)
    shadeAndImplementation(libs.kotlinx.collections.immutable)

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

sourceSets.main {
    kotlin.srcDir("../common-forge/src/main/kotlin")
    resources.srcDir("../resources/src/main/resources/lang")
    resources.srcDir("../resources/src/main/resources/icon")
    resources.srcDir("../resources/src/main/resources/textures")
}

tasks.processResources {
    val replaceProperties = mapOf(
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_version" to version,
    )

    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(replaceProperties)
    }
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_1_8)
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Jar> {
    from(File(rootDir, "LICENSE")) {
        rename { "${it}_${modName}" }
    }
}

tasks.shadowJar {
    archiveClassifier = ""
    configurations.set(setOf(project.configurations.shadow.get()))
}

reobf {
    create("shadowJar")
}

tasks.jar {
    finalizedBy("reobfShadowJar")
}
tasks.create<RenameJarInPlace>("reobfJar").dependsOn("shadowJar")
