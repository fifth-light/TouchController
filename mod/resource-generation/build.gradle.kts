plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

group = "top.fifthlight.touchcontroller"

val outputDir = layout.buildDirectory.dir("generated")

val languageFile = File(project(":mod:common").projectDir, "src/main/resources/touchcontroller/lang/en_us.json")
task<JavaExec>("generateTextBindings") {
    dependsOn(tasks.compileKotlin)

    inputs.files(languageFile)
    outputs.dir(outputDir)

    group = "build"
    description = "Generate binding for translation file"
    mainClass = "top.fifthlight.touchcontroller.resource.TextsKt"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf(languageFile.toString(), outputDir.get().toString())
}

val textureDir = File(project(":mod:common").projectDir, "src/main/resources/touchcontroller/textures")
task<JavaExec>("generateTextureBindings") {
    dependsOn(tasks.compileKotlin)

    inputs.dir(textureDir)
    outputs.dir(outputDir)

    group = "build"
    description = "Generate bindings for texture files"
    mainClass = "top.fifthlight.touchcontroller.resource.TexturesKt"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf(textureDir.toString(), outputDir.get().toString())
}

task("generate") {
    dependsOn("generateTextBindings", "generateTextureBindings")

    group = "build"
    description = "Generate all source files"
}

dependencies {
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinx.serialization.json)
}

kotlin {
    jvmToolchain(12)
}
