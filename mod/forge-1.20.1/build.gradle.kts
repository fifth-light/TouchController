import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    idea
    alias(libs.plugins.forge.gradle)
    alias(libs.plugins.parchmentmc.librarian.forgegradle)
    alias(libs.plugins.mixin)
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.gr8)
}

version = "0.0.13"
group = "top.fifthlight.touchcontroller"

val modName = "TouchController"
val modId = "touchcontroller"

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

configurations.create("shadow")

tasks.jar {
    archiveBaseName = "$modName-slim"
}

fun DependencyHandlerScope.shade(dependency: Any) {
    add("shadow", dependency)
}

fun DependencyHandlerScope.shadeAndImplementation(dependency: Any) {
    shade(dependency)
    implementation(dependency)
    minecraftLibrary(dependency)
}

dependencies {
    minecraft("net.minecraftforge:forge:1.20.1-47.3.0")

    shadeAndImplementation(project(":common-data"))
    shadeAndImplementation(project(":mod:common"))

    shade(project(":proxy-windows"))
    shade(project(":proxy-server-android"))
    shadeAndImplementation(project(":proxy-client"))
    shadeAndImplementation(project(":proxy-server"))

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

tasks.compileJava {
    dependsOn("createMcpToSrg")
    dependsOn("extractSrg")
}

gr8 {
    val shadowedJar = create("gr8") {
        addProgramJarsFrom(configurations.getByName("shadow"))
        addProgramJarsFrom(tasks.getByName("jar"))

        addClassPathJarsFrom(configurations.getByName("runtimeClasspath"))

        r8Version("8.8.20")
        proguardFile("rules.pro")
    }

    replaceOutgoingJar(shadowedJar)
}

tasks.register<Jar>("gr8Jar") {
    dependsOn("reobfJar")

    inputs.files(tasks.getByName("gr8Gr8ShadowedJar").outputs.files)
    archiveBaseName = "$modName-noreobf"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    val jarFile =
        tasks.getByName("gr8Gr8ShadowedJar").outputs.files.first { it.extension.equals("jar", ignoreCase = true) }
    from(zipTree(jarFile))
}

tasks.getByName("gr8Gr8ShadowedJar") {
    dependsOn("addMixinsToJar")
}

reobf {
    create("gr8Jar") {
        // Use mapping from compileJava, to avoid problems of @Shadow
        extraMappings.from("build/tmp/compileJava/compileJava-mappings.tsrg")
    }
}

tasks.register<Copy>("renameOutputJar") {
    dependsOn("reobfGr8Jar")
    from("build/reobfGr8Jar/output.jar") {
        rename {
            "$modName-$version.jar"
        }
    }
    destinationDir = layout.buildDirectory.dir("libs").get().asFile
}

tasks.assemble {
    dependsOn("reobfGr8Jar")
    dependsOn("renameOutputJar")
}
