package top.fifthlight.touchcontroller.resource

import com.squareup.kotlinpoet.*
import java.io.File

fun main(args: Array<String>) {
    val (output, properties) = args

    val outputDir = File(output)
    outputDir.mkdirs()

    val buildInfo = TypeSpec.objectBuilder("BuildInfo").apply {
        properties.split("\n").filter { it.isNotBlank() }.forEach { line ->
            val (name, value) = line.split(":")
            val transformedName = buildString {
                name.trim().forEach {
                    if (it.isUpperCase()) {
                        append('_')
                    }
                    append(it.uppercase())
                }
            }
            addProperty(
                PropertySpec
                    .builder(transformedName, String::class)
                    .initializer("%S", value.trim())
                    .addModifiers(KModifier.CONST)
                    .build()
            )
        }
    }.build()

    val file = FileSpec
        .builder("top.fifthlight.touchcontroller", "BuildInfo")
        .addAnnotation(
            AnnotationSpec
                .builder(Suppress::class)
                .addMember("%S", "RedundantVisibilityModifier")
                .build()
        )
        .addType(buildInfo)
        .build()
    file.writeTo(outputDir)
}