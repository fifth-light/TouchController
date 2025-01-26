import com.gradleup.gr8.Gr8Task
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.DependencyHandlerScope

plugins {
    idea
    eclipse
    java
    id("fabric-loom")
    id("com.gradleup.gr8")
}

val libs = the<LibrariesForLibs>()

val modId: String by extra.properties
val modName: String by extra.properties
val modVersion: String by extra.properties
val modDescription: String by extra.properties
val modLicense: String by extra.properties
val modHomepage: String by extra.properties
val modAuthors: String by extra.properties
val modContributors: String by extra.properties
val modSource: String by extra.properties
val modIssueTracker: String by extra.properties
val javaVersion: String by extra.properties
val gameVersion: String by extra.properties
val yarnVersion: String by extra.properties
val fabricApiVersion: String by extra.properties
val modmenuVersion: String by extra.properties
val bridgeSlf4j: String by extra.properties
val bridgeSlf4jBool = bridgeSlf4j.toBoolean()
val excludeR8: String by extra.properties

version = "$modVersion+fabric-$gameVersion"
group = "top.fifthlight.touchcontroller"

configurations.create("shadow")

tasks.jar {
    archiveBaseName = "$modName-slim"
}

fun DependencyHandlerScope.shade(dependency: Any) {
    add("shadow", dependency)
}

fun DependencyHandlerScope.shade(
    dependency: String,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit,
) {
    add("shadow", dependency, dependencyConfiguration)
}

fun <T : ModuleDependency> DependencyHandlerScope.shade(
    dependency: T,
    dependencyConfiguration: T.() -> Unit,
) {
    add("shadow", dependency, dependencyConfiguration)
}

fun DependencyHandlerScope.shadeAndImplementation(dependency: Any) {
    shade(dependency)
    implementation(dependency)
}

fun DependencyHandlerScope.shadeAndImplementation(
    dependency: String,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit
) {
    shade(dependency, dependencyConfiguration)
    implementation(dependency, dependencyConfiguration)
}

fun <T : ModuleDependency> DependencyHandlerScope.shadeAndImplementation(
    dependency: T,
    dependencyConfiguration: T.() -> Unit,
) {
    shade(dependency, dependencyConfiguration)
    implementation(dependency, dependencyConfiguration)
}

dependencies {
    minecraft("com.mojang:minecraft:$gameVersion")
    mappings("net.fabricmc:yarn:$yarnVersion:v2")
    modImplementation(libs.fabric.loader)

    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    modImplementation("com.terraformersmc:modmenu:$modmenuVersion")

    shadeAndImplementation(project(":mod:common")) {
        exclude("org.slf4j")
    }
    shadeAndImplementation(project(":combine"))
    if (bridgeSlf4jBool) {
        shadeAndImplementation(project(":log4j-slf4j2-impl")) {
            exclude("org.apache.logging.log4j")
        }
    }

    shade(project(":proxy-windows"))
    shade(project(":proxy-server-android"))
}

tasks.processResources {
    val modAuthorsList = modAuthors.split(",").map(String::trim).filter(String::isNotEmpty)
    val modContributorsList = modContributors.split(",").map(String::trim).filter(String::isNotEmpty)
    fun String.quote(quoteStartChar: Char = '"', quoteEndChar: Char = '"') = quoteStartChar + this + quoteEndChar
    val modAuthorsArray = modAuthorsList.joinToString(", ", transform = String::quote).drop(1).dropLast(1)
    val modContributorsArray = modContributorsList.joinToString(", ", transform = String::quote).drop(1).dropLast(1)

    val (major, minor, patch) = gameVersion.split(".")
    // Fabric API changed its mod ID to "fabric-api" in version 1.19.2
    val fabricApiName = when {
        major.toInt() != 1 -> error("Whoa? Minecraft 2?")
        minor.toInt() > 19 -> "fabric-api"
        minor.toInt() == 19 && patch.toInt() >= 2 -> "fabric-api"
        else -> "fabric"
    }

    val properties = mapOf(
        "mod_id" to modId,
        "mod_version_full" to version,
        "mod_name" to modName,
        "mod_description" to modDescription,
        "mod_license" to modLicense,
        "mod_homepage" to modHomepage,
        "mod_authors_string" to modAuthors,
        "mod_contributors_string" to modContributors,
        "mod_authors_array" to modAuthorsArray,
        "mod_contributors_array" to modContributorsArray,
        "mod_source" to modSource,
        "mod_issue_tracker" to modIssueTracker,
        "fabric_loader_version" to libs.versions.fabric.loader.get(),
        "game_version" to gameVersion,
        "java_version" to javaVersion,
        "fabric_api_name" to fabricApiName,
        "fabric_api_version" to fabricApiVersion,
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

val minecraftShadow = configurations.create("minecraftShadow") {
    excludeR8.split(",").filter(String::isNotEmpty).forEach {
        if (it.contains(":")) {
            val (group, module) = it.split(":")
            exclude(group, module)
        } else {
            exclude(it)
        }
    }
    extendsFrom(configurations.compileClasspath.get())
}

gr8 {
    create("gr8") {
        addProgramJarsFrom(configurations.getByName("shadow"))
        addProgramJarsFrom(tasks.jar)

        addClassPathJarsFrom(minecraftShadow)

        r8Version("8.9.21")
        proguardFile(rootProject.file("mod/common-fabric/rules.pro"))
    }
}

tasks.withType<Gr8Task> {
    jvmArgs("-Xmx512M")
}

tasks.remapJar {
    dependsOn("gr8Gr8ShadowedJar")

    val jarFile = tasks.getByName("gr8Gr8ShadowedJar").outputs.files
        .first { it.extension.equals("jar", ignoreCase = true) }

    inputFile = jarFile
    archiveBaseName = "$modName-fat"
    addNestedDependencies = false
}

val copyJarTask = tasks.register<Jar>("copyJar") {
    dependsOn(tasks.remapJar)
    archiveBaseName = modName
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true

    val excludeWhitelist = listOf("org.slf4j.spi.SLF4JServiceProvider")
    from(zipTree(tasks.remapJar.get().archiveFile)) {
        exclude { file ->
            val path = file.path
            if (path.startsWith("META-INF")) {
                !excludeWhitelist.any { path.endsWith(it) }
            } else {
                path == "module-info.class"
            }
        }
    }
}

tasks.assemble {
    dependsOn(copyJarTask)
}