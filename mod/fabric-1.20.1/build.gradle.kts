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

val modmenuVersion = "7.2.2"

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("touchcontroller")
    versionType.set("alpha")
    uploadFile.set(tasks.remapJar)
    gameVersions.add("1.20.1")
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
    minecraft("com.mojang:minecraft:1.20.1")
    mappings("net.fabricmc:yarn:1.20.1+build.10:v2")
    modImplementation(libs.fabric.loader)

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.92.2+1.20.1")
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
    includeAndImplementation(libs.koin.compose)
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
    resources.srcDir("../resources/src/main/resources/lang")
    resources.srcDir("../resources/src/main/resources/icon")
    resources.srcDir("../resources/src/main/resources/textures")
}

kotlin {
    jvmToolchain(17)
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
