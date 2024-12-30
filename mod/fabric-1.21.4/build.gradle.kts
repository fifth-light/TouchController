plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.modrinth.minotaur)
    alias(libs.plugins.compose.compiler)
}

version = "0.0.13"
group = "top.fifthlight.touchcontroller"

var modName = "TouchController"
base {
    archivesName = modName
}

val modmenuVersion = "13.0.0-beta.1"

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("touchcontroller")
    versionType.set("alpha")
    uploadFile.set(tasks.remapJar)
    gameVersions.add("1.21.4")
    dependencies {
        required.project("fabric-api")
        required.version("fabric-language-kotlin", libs.versions.fabric.language.kotlin.get())
        optional.version("modmenu", modmenuVersion)
    }
}

fun DependencyHandlerScope.includeAndImplementation(dependencyNotation: Any) {
    include(dependencyNotation)
    implementation(dependencyNotation)
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.4")
    mappings("net.fabricmc:yarn:1.21.4+build.2:v2")
    modImplementation(libs.fabric.loader)

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.113.0+1.21.4")
    modImplementation(libs.fabric.language.kotlin)
    modImplementation("com.terraformersmc:modmenu:$modmenuVersion")

    includeAndImplementation(project(":common-data"))
    includeAndImplementation(project(":mod:common"))

    include(project(":proxy-windows"))
    include(project(":proxy-server-android"))
    includeAndImplementation(project(":proxy-client"))
    includeAndImplementation(project(":proxy-server"))

    include(libs.androidx.collection)
    includeAndImplementation(libs.compose.runtime)
    includeAndImplementation(project(":combine"))

    includeAndImplementation(libs.koin.core)
    includeAndImplementation(libs.koin.logger.slf4j)
    includeAndImplementation(libs.kotlinx.collections.immutable)
}

tasks.withType<ProcessResources> {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to project.version,
                "name" to modName,
            )
        )
    }
}

sourceSets.main {
    kotlin.srcDir("../common-fabric/src/main/kotlin")
    kotlin.srcDir("../common-fabric-1.21/src/main/kotlin")
    java.srcDir("../common-fabric-1.21/src/main/java")
    resources.srcDir("../resources/src/main/resources/lang")
    resources.srcDir("../resources/src/main/resources/icon")
    resources.srcDir("../resources/src/main/resources/textures")
}

kotlin {
    jvmToolchain(21)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }

    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Jar> {
    from(File(rootDir, "LICENSE")) {
        rename { "${it}_${modName}" }
    }
}
