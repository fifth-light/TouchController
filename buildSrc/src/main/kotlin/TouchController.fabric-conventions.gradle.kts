import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    idea
    eclipse
    java
    id("fabric-loom")
}

val libs = the<LibrariesForLibs>()

fun DependencyHandlerScope.includeAndImplementation(dependencyNotation: Any) {
    include(dependencyNotation)
    implementation(dependencyNotation)
}

val modId: String by extra.properties
val modName: String by extra.properties
val modVersion: String by extra.properties
val modDescription: String by extra.properties
val modLicense: String by extra.properties
val modHomepage: String by extra.properties
val modSource: String by extra.properties
val javaVersion: String by extra.properties
val gameVersion: String by extra.properties
val yarnVersion: String by extra.properties
val fabricApiVersion: String by extra.properties
val modmenuVersion: String by extra.properties

version = "$modVersion+fabric-$gameVersion"
group = "top.fifthlight.touchcontroller"

base {
    archivesName = modName
}

dependencies {
    minecraft("com.mojang:minecraft:$gameVersion")
    mappings("net.fabricmc:yarn:$yarnVersion:v2")
    modImplementation(libs.fabric.loader)

    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    modImplementation(libs.fabric.language.kotlin)
    modImplementation("com.terraformersmc:modmenu:$modmenuVersion")

    includeAndImplementation(project(":mod:common"))

    include(project(":common-data"))
    include(project(":proxy-windows"))
    include(project(":proxy-server-android"))
    include(project(":proxy-client"))
    include(project(":proxy-server"))

    include(project(":combine"))
    include(libs.androidx.collection)
    include(libs.compose.runtime)

    include(libs.koin.core)
    include(libs.koin.compose)
    include(libs.koin.logger.slf4j)
    include(libs.kotlinx.collections.immutable)
}

tasks.processResources {
    val properties = mapOf(
        "mod_id" to modId,
        "mod_version_full" to version,
        "mod_name" to modName,
        "mod_description" to modDescription,
        "mod_license" to modLicense,
        "mod_homepage" to modHomepage,
        "mod_source" to modSource,
        "fabric_loader_version" to libs.versions.fabric.loader.get(),
        "game_version" to gameVersion,
        "java_version" to javaVersion,
        "fabric_api_version" to fabricApiVersion,
        "fabric_language_kotlin_version" to libs.versions.fabric.language.kotlin.get(),
    )

    inputs.properties(properties)

    filesMatching("fabric.mod.json") {
        expand(properties)
    }
}

sourceSets.main {
    resources.srcDir("../common-fabric/src/main/resources")
    resources.srcDir("../resources/src/main/resources/lang")
    resources.srcDir("../resources/src/main/resources/icon")
    resources.srcDir("../resources/src/main/resources/textures")
}

tasks.withType<Jar> {
    from(File(rootDir, "LICENSE")) {
        rename { "${it}_${modName}" }
    }
}
