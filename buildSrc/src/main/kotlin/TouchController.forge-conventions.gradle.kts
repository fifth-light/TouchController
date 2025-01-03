import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.*
import kotlin.collections.first
import kotlin.collections.getValue
import kotlin.collections.listOf
import kotlin.collections.mapOf
import kotlin.collections.set

plugins {
    idea
    eclipse
    java
    id("net.minecraftforge.gradle")
    id("com.gradleup.gr8")
    id("org.parchmentmc.librarian.forgegradle")
    id("org.spongepowered.mixin")
}

val libs = the<LibrariesForLibs>()

val modId: String by extra.properties
val modName: String by extra.properties
val modVersion: String by extra.properties
val modDescription: String by extra.properties
val javaVersion: String by extra.properties
val javaVersionNum = javaVersion.toInt()
val gameVersion: String by extra.properties
val forgeVersion: String by extra.properties
val parchmentVersion: String by extra.properties

version = modVersion
group = "top.fifthlight.touchcontroller"

minecraft {
    mappings("parchment", parchmentVersion)

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
    minecraft("net.minecraftforge:forge:$gameVersion-$forgeVersion")

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
    resources.srcDir("../resources/src/main/resources/lang")
    resources.srcDir("../resources/src/main/resources/icon")
    resources.srcDir("../resources/src/main/resources/textures")
}

tasks.processResources {
    val properties = mapOf(
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_version" to version,
        "forge_version" to forgeVersion,
        "mod_description" to modDescription,
        "game_version" to gameVersion,
    )

    inputs.properties(properties)

    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(properties)
    }
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
        proguardFile(rootProject.file("mod/common-forge/rules.pro"))
    }

    replaceOutgoingJar(shadowedJar)
}

// Create a Jar task to exclude some META-INF files from R8 output, and make ForgeGradle
// reobf task happy (FG requires JarTask for it's reobf input)
tasks.register<Jar>("gr8Jar") {
    dependsOn("reobfJar")

    inputs.files(tasks.getByName("gr8Gr8ShadowedJar").outputs.files)
    archiveBaseName = "$modName-noreobf"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    val jarFile =
        tasks.getByName("gr8Gr8ShadowedJar").outputs.files.first { it.extension.equals("jar", ignoreCase = true) }

    from(zipTree(jarFile)) {
        exclude("DebugProbesKt.bin")
        exclude("kotlin")
        exclude {
            it.path.startsWith("META-INF") && !it.path.endsWith("mods.toml")
        }
    }
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
