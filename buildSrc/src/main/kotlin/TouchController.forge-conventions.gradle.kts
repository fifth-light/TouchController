import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.spongepowered.asm.gradle.plugins.MixinExtension.AddMixinsToJarTask

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
val modLicense: String by extra.properties
val modLicenseLink: String by extra.properties
val modIssueTracker: String by extra.properties
val modHomepage: String by extra.properties
val modAuthors: String by extra.properties
val modContributors: String by extra.properties
val gameVersion: String by extra.properties
val forgeVersion: String by extra.properties
val mappingType: String by extra.properties
val useMixin: String by extra.properties
val useMixinBool = useMixin.toBoolean()
val useAccessTransformer: String by extra.properties
val useAccessTransformerBool = useAccessTransformer.toBoolean()
val bridgeSlf4j: String by extra.properties
val bridgeSlf4jBool = bridgeSlf4j.toBoolean()
val legacyLanguageFormat: String by extra.properties
val legacyLanguageFormatBool = legacyLanguageFormat.toBoolean()
val shadeJoml: String by extra.properties
val shadeJomlBool = shadeJoml.toBoolean()
val excludeR8: String by extra.properties

version = "$modVersion+forge-$gameVersion"
group = "top.fifthlight.touchcontroller"

minecraft {
    when (mappingType) {
        "official" -> {
            val parchmentVersion: String by extra.properties
            mappings("parchment", parchmentVersion)
        }

        "mcp-snapshot" -> {
            val mcpVersion: String by extra.properties
            mappings("snapshot", mcpVersion)
        }
    }

    if (useAccessTransformerBool) {
        accessTransformer(file("src/main/resources/META-INF/touchcontroller_at.cfg"))
    }

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

if (!useMixinBool) {
    tasks.withType<AddMixinsToJarTask> {
        enabled = false
    }
}

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
    minecraftLibrary(dependency)
}

fun DependencyHandlerScope.shadeAndImplementation(
    dependency: String,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit
) {
    shade(dependency, dependencyConfiguration)
    implementation(dependency, dependencyConfiguration)
    minecraftLibrary(dependency, dependencyConfiguration)
}

fun <T : ModuleDependency> DependencyHandlerScope.shadeAndImplementation(
    dependency: T,
    dependencyConfiguration: T.() -> Unit,
) {
    shade(dependency, dependencyConfiguration)
    implementation(dependency, dependencyConfiguration)
    minecraftLibrary(dependency, dependencyConfiguration)
}

dependencies {
    minecraft("net.minecraftforge:forge:$gameVersion-$forgeVersion")

    shadeAndImplementation(project(":mod:common")) {
        exclude("org.slf4j")
    }
    shadeAndImplementation(project(":combine"))
    if (bridgeSlf4jBool) {
        shadeAndImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.19.0") {
            exclude("org.apache.logging.log4j")
        }
    }
    if (shadeJomlBool) {
        shadeAndImplementation(libs.joml)
    }

    shade(project(":proxy-windows"))
    shade(project(":proxy-server-android"))

    if (useMixinBool) {
        annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    }
}

sourceSets.main {
    if (!legacyLanguageFormatBool) {
        resources.srcDir("../resources/src/main/resources/lang")
    }
    resources.srcDir("../resources/src/main/resources/textures")
}

tasks.processResources {
    from("../resources/src/main/resources/icon/assets/touchcontroller/icon.png")
    if (legacyLanguageFormatBool) {
        dependsOn(":mod:resources:generateLegacyText")
        from(project(":mod:resources").layout.buildDirectory.dir("generated/resources/legacy-lang"))
    }

    val modAuthorsList = modAuthors.split(",").map(String::trim).filter(String::isNotEmpty)
    val modContributorsList = modContributors.split(",").map(String::trim).filter(String::isNotEmpty)
    fun String.quote(quoteStartChar: Char = '"', quoteEndChar: Char = '"') = quoteStartChar + this + quoteEndChar
    val modAuthorsArray = modAuthorsList.joinToString(", ", transform = String::quote).drop(1).dropLast(1)
    val modContributorsArray = modContributorsList.joinToString(", ", transform = String::quote).drop(1).dropLast(1)

    val loaderVersion = forgeVersion.substringBefore('.')
    val properties = mapOf(
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_version_full" to version,
        "mod_license" to modLicense,
        "mod_license_link" to modLicenseLink,
        "mod_issue_tracker" to modIssueTracker,
        "mod_homepage" to modHomepage,
        "mod_authors_string" to modAuthors,
        "mod_contributors_string" to modContributors,
        "mod_authors_array" to modAuthorsArray,
        "mod_contributors_array" to modContributorsArray,
        "forge_version" to forgeVersion,
        "mod_description" to modDescription,
        "game_version" to gameVersion,
        "loader_version" to loaderVersion,
    )

    inputs.properties(properties)

    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta", "mcmod.info")) {
        expand(properties)
    }
}

tasks.withType<Jar> {
    from(File(rootDir, "LICENSE")) {
        rename { "${it}_${modName}" }
    }

    manifest {
        val attributes = mutableMapOf(
            "FMLCorePlugin" to "top.fifthlight.touchcontroller.TouchControllerCorePlugin",
            "FMLCorePluginContainsFMLMod" to "true",
        )
        if (useAccessTransformerBool) {
            attributes += ("FMLAT" to "touchcontroller_at.cfg")
        }
        attributes(attributes)
    }
}

tasks.compileJava {
    dependsOn("createMcpToSrg")
    dependsOn("extractSrg")
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
    extendsFrom(configurations.minecraft.get())
}

gr8 {
    val shadowedJar = create("gr8") {
        addProgramJarsFrom(configurations.getByName("shadow"))
        addProgramJarsFrom(tasks.jar)

        addClassPathJarsFrom(minecraftShadow)

        r8Version("8.9.21")
        proguardFile(rootProject.file("mod/common-forge/rules.pro"))
    }

    replaceOutgoingJar(shadowedJar)
}

// Create a Jar task to exclude some META-INF files and module-info.class from R8 output,
// and make ForgeGradle reobf task happy (FG requires JarTask for it's reobf input)
tasks.register<Jar>("gr8Jar") {
    dependsOn("reobfJar")

    inputs.files(tasks.getByName("gr8Gr8ShadowedJar").outputs.files)
    archiveBaseName = "$modName-noreobf"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    val jarFile =
        tasks.getByName("gr8Gr8ShadowedJar").outputs.files.first { it.extension.equals("jar", ignoreCase = true) }

    val excludeWhitelist = listOf(
        "touchcontroller_at.cfg",
        "mods.toml",
        "org.slf4j.spi.SLF4JServiceProvider",
    )
    from(zipTree(jarFile)) {
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

tasks.getByName("gr8Gr8ShadowedJar") {
    dependsOn("addMixinsToJar")
}

reobf {
    create("gr8Jar") {
        if (useMixinBool) {
            // Use mapping from compileJava, to avoid problems of @Shadow
            extraMappings.from("build/tmp/compileJava/compileJava-mappings.tsrg")
        }
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
