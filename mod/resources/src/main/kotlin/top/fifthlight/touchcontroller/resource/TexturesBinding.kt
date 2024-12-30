package top.fifthlight.touchcontroller.resource

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.nio.file.FileVisitResult
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.io.path.relativeTo
import kotlin.io.path.visitFileTree

fun main(args: Array<String>) {
    val (texturesDirPath, atlasFilePath, textureOutput) = args

    val outputDir = File(textureOutput)
    outputDir.mkdirs()

    val atlasFile = Path.of(atlasFilePath)

    @OptIn(ExperimentalSerializationApi::class)
    val atlasTextures: Map<String, PlacedTexture> = Json.decodeFromStream(atlasFile.inputStream())

    val texturesBuilder = TypeSpec.objectBuilder("Textures")
    val texturesDir = Path.of(texturesDirPath)
    texturesDir.visitFileTree {
        onVisitFile { file, _ ->
            if (file.fileName.toString().endsWith(".png", true)) {
                val relativePath = file.relativeTo(texturesDir)
                val transformedPath = relativePath.joinToString("_").uppercase().removeSuffix(".PNG")
                val texturePath = relativePath.joinToString("/")
                val placed = atlasTextures[transformedPath] ?: error("Texture $transformedPath not found in atlas file")

                texturesBuilder.addProperty(
                    PropertySpec
                        .builder(transformedPath, ClassName("top.fifthlight.combine.data", "Texture"))
                        .initializer(
                            """
                            Texture(
                                identifier = Identifier.Namespaced(%S, %S),
                                size = IntSize(
                                    width = %L,
                                    height = %L
                                ),
                                atlasOffset = IntOffset(
                                    x = %L,
                                    y = %L
                                ),
                            )""".trimIndent(),
                            "touchcontroller",
                            "textures/$texturePath",
                            placed.size.width,
                            placed.size.height,
                            placed.position.x,
                            placed.position.y,
                        )
                        .build()
                )
            }
            FileVisitResult.CONTINUE
        }
    }

    val textures = texturesBuilder.build()
    val file = FileSpec
        .builder("top.fifthlight.touchcontroller.assets", "Textures")
        .addImport("top.fifthlight.combine.data", "Identifier")
        .addImport("top.fifthlight.data", "IntSize", "IntOffset")
        .addType(textures)
        .build()
    file.writeTo(outputDir)
}