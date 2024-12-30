plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

group = "top.fifthlight.touchcontroller"

val outputDir = layout.buildDirectory.dir("generated")
val outputKotlinDir = layout.buildDirectory.dir("generated/kotlin")

val languageFile = File(projectDir, "src/main/resources/lang/assets/touchcontroller/lang/en_us.json")
task<JavaExec>("generateTextBindings") {
    dependsOn(tasks.compileKotlin)

    inputs.files(languageFile)
    outputs.dir(outputKotlinDir)

    group = "build"
    description = "Generate binding for translation file"
    mainClass = "top.fifthlight.touchcontroller.resource.TextsBindingKt"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf(languageFile.toString(), outputKotlinDir.get().toString())
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
    outputs.dir(outputKotlinDir)

    group = "build"
    description = "Generate bindings for texture files"
    mainClass = "top.fifthlight.touchcontroller.resource.TexturesBindingKt"
    classpath = sourceSets["main"].runtimeClasspath
    args =
        listOf(textureDir.toString(), outputGuiTextureAtlasJsonFile.get().toString(), outputKotlinDir.get().toString())
}

task("generate") {
    dependsOn("generateTextBindings", "generateTextureAtlas", "generateTextureBindings")

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
