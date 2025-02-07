package top.fifthlight.touchcontroller.resource

import com.squareup.kotlinpoet.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {
    val (languageFile, languageOutput) = args

    val outputDir = File(languageOutput)
    outputDir.mkdirs()

    val map: Map<String, String> = Json.decodeFromStream(File(languageFile).inputStream())

    val textsBuilder = TypeSpec.objectBuilder("Texts")

    for ((key, value) in map) {
        if (!key.startsWith("touchcontroller.")) {
            System.err.println("Key $key don't start with touchcontroller, skip it.")
            continue
        }
        val strippedKey = key.removePrefix("touchcontroller.")
        val transformedKey = strippedKey.uppercase().replace('.', '_')

        textsBuilder.addProperty(
            PropertySpec
                .builder(transformedKey, ClassName("top.fifthlight.combine.data", "Identifier"))
                .addKdoc("Translation text: %L", value)
                .initializer("Identifier.Namespaced(%S, %S)", "touchcontroller", strippedKey)
                .build()
        )
    }

    val texts = textsBuilder.build()
    val file = FileSpec
        .builder("top.fifthlight.touchcontroller.assets", "Texts")
        .addAnnotation(
            AnnotationSpec
                .builder(Suppress::class)
                .addMember("%S", "RedundantVisibilityModifier")
                .build()
        )
        .addType(texts)
        .build()
    file.writeTo(outputDir)
}
