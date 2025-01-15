plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

group = "top.fifthlight.touchcontroller"

val outputKotlinResourceDir = layout.buildDirectory.dir("generated/kotlin/resources")

val languageDir = File(projectDir, "src/main/resources/lang/assets/touchcontroller/lang")

val defaultLanguageFile = File(languageDir, "en_us.json")
task<JavaExec>("generateTextBindings") {
    dependsOn(tasks.compileKotlin)

    inputs.files(defaultLanguageFile)
    outputs.dir(outputKotlinResourceDir)

    group = "build"
    description = "Generate binding for translation file"
    mainClass = "top.fifthlight.touchcontroller.resource.TextsBindingKt"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf(defaultLanguageFile.toString(), outputKotlinResourceDir.get().toString())
}

val legacyLanguageDir = layout.buildDirectory.file("generated/resources/legacy-lang/assets/touchcontroller/lang")
task<JavaExec>("generateLegacyText") {
    dependsOn(tasks.compileKotlin)

    inputs.dir(languageDir)
    outputs.dir(legacyLanguageDir)

    group = "build"
    description = "Generate legacy translation file"
    mainClass = "top.fifthlight.touchcontroller.resource.LegacyTextGeneratorKt"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf(languageDir.toString(), legacyLanguageDir.get().toString())
}

val textureDir = File(projectDir, "src/main/resources/textures/assets/touchcontroller/textures")

val outputGuiTextureAtlasJsonFile = layout.buildDirectory.file("generated/resources/atlas.json")
val outputGuiTextureAtlasFile =
    layout.buildDirectory.file("generated/resources/altas/assets/touchcontroller/textures/gui/atlas.png")
task<JavaExec>("generateTextureAtlas") {
    dependsOn(tasks.compileKotlin)

    inputs.dir(textureDir)
    outputs.files(outputGuiTextureAtlasFile, outputGuiTextureAtlasJsonFile)

    group = "build"
    description = "Generate texture atlas"
    mainClass = "top.fifthlight.touchcontroller.resource.TexturesAtlasKt"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf(
        textureDir.toString(),
        outputGuiTextureAtlasFile.get().toString(),
        outputGuiTextureAtlasJsonFile.get().toString()
    )
}

task<JavaExec>("generateTextureBindings") {
    dependsOn(tasks.compileKotlin, "generateTextureAtlas")

    inputs.dir(textureDir)
    outputs.dir(outputKotlinResourceDir)

    group = "build"
    description = "Generate bindings for texture files"
    mainClass = "top.fifthlight.touchcontroller.resource.TexturesBindingKt"
    classpath = sourceSets["main"].runtimeClasspath
    args =
        listOf(
            textureDir.toString(),
            outputGuiTextureAtlasJsonFile.get().toString(),
            outputKotlinResourceDir.get().toString()
        )
}

val outputKotlinBuildInfoDir = layout.buildDirectory.dir("generated/kotlin/buildinfo")
task<JavaExec>("generateBuildInfo") {
    dependsOn(tasks.compileKotlin)

    val modId: String by properties
    val modName: String by properties
    val modVersion: String by properties
    val properties = mapOf(
        "modId" to modId,
        "modName" to modName,
        "modVersion" to modVersion
    )
    inputs.properties(properties)
    outputs.dir(outputKotlinBuildInfoDir)

    group = "build"
    description = "Generate build information"
    mainClass = "top.fifthlight.touchcontroller.resource.BuildInfoGeneratorKt"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf(
        outputKotlinBuildInfoDir.get().toString(),
        properties.entries.joinToString("\n") { (key, value) -> "$key: $value" }
    )
}

task("generate") {
    dependsOn(
        "generateTextBindings",
        "generateTextureAtlas",
        "generateTextureBindings",
        "generateBuildInfo",
        "generateLegacyText"
    )

    group = "build"
    description = "Generate all source files"
}

dependencies {
    implementation(project(":common-data"))
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinx.serialization.json)
}

kotlin {
    jvmToolchain(21)
}
