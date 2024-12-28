package top.fifthlight.touchcontroller.resource

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import java.nio.file.FileVisitResult
import java.nio.file.Path
import kotlin.io.path.relativeTo
import kotlin.io.path.visitFileTree

fun main(args: Array<String>) {
    val (texturesDirPath, textureOutput) = args

    val outputDir = File(textureOutput)
    outputDir.mkdirs()

    val texturesBuilder = TypeSpec.objectBuilder("Textures")

    val texturesDir = Path.of(texturesDirPath)
    texturesDir.visitFileTree {
        onVisitFile { file, _ ->
            if (file.fileName.toString().endsWith(".png", true)) {
                val relativePath = file.relativeTo(texturesDir)
                val transformedPath = relativePath.joinToString("_").uppercase().removeSuffix(".PNG")
                val texturePath = relativePath.joinToString("/")

                texturesBuilder.addProperty(
                    PropertySpec
                        .builder(transformedPath, ClassName("top.fifthlight.combine.data", "Identifier"))
                        .initializer("Identifier.Namespaced(%S, %S)", "touchcontroller", "textures/$texturePath")
                        .build()
                )
            }
            FileVisitResult.CONTINUE
        }
    }

    val textures = texturesBuilder.build()
    val file = FileSpec
        .builder("top.fifthlight.touchcontroller.assets", "Textures")
        .addType(textures)
        .build()
    file.writeTo(outputDir)
}