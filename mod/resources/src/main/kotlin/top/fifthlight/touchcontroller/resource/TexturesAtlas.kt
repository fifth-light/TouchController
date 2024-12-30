package top.fifthlight.touchcontroller.resource

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import top.fifthlight.data.IntOffset
import top.fifthlight.data.IntSize
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.outputStream
import kotlin.io.path.relativeTo
import kotlin.io.path.visitFileTree
import kotlin.math.max

private fun Path.makeParentDirs() {
    Files.createDirectories(parent)
}

private data class Texture(
    val path: Path,
    val transformedPath: String,
    val image: BufferedImage,
) {
    val size: IntSize
        get() = IntSize(
            image.width,
            image.height,
        )

    fun place(position: IntOffset) = PlacedTexture(
        position = position,
        size = size,
    )
}

fun main(args: Array<String>) {
    val (textureDirPath, outputGuiTextureAtlasPath, outputGuiTextureAtlasJsonPath) = args

    val textureDir = Path.of(textureDirPath)
    val outputGuiTextureAtlasFile = Path.of(outputGuiTextureAtlasPath)
    val outputGuiTextureAtlasJsonFile = Path.of(outputGuiTextureAtlasJsonPath)

    val textures = arrayListOf<Texture>()
    textureDir.visitFileTree {
        onVisitFile { file, _ ->
            if (file.fileName.toString().endsWith(".png", true)) {
                val relativePath = file.relativeTo(textureDir)
                val transformedPath = relativePath.joinToString("_").uppercase().removeSuffix(".PNG")
                val image = ImageIO.read(file.toFile())
                textures.add(
                    Texture(
                        path = file,
                        transformedPath = transformedPath,
                        image = image,
                    )
                )
            }
            FileVisitResult.CONTINUE
        }
    }
    textures.sortByDescending { texture ->
        texture.size.width * texture.size.height
    }

    val textureSize = IntSize(256, 256)
    val outputImage = BufferedImage(textureSize.width, textureSize.height, TYPE_INT_ARGB)
    val outputGraphics = outputImage.createGraphics()
    val placedTextures = hashMapOf<String, PlacedTexture>()
    var cursorPosition = IntOffset(0, 0)
    var maxLineHeight = 0
    for (texture in textures) {
        if (texture.size.width > textureSize.width) {
            error("Texture ${texture.transformedPath} too big: ${texture.size}")
        }
        if (texture.size.height + cursorPosition.y > textureSize.height) {
            error("No space left for texture ${texture.transformedPath}")
        }
        if (cursorPosition.x + texture.size.width > textureSize.width) {
            if (maxLineHeight == 0) {
                error("Texture ${texture.transformedPath} too big: ${texture.size}")
            }
            cursorPosition = IntOffset(0, cursorPosition.y + maxLineHeight)
            maxLineHeight = 0
        }
        maxLineHeight = max(maxLineHeight, texture.size.height)
        placedTextures[texture.transformedPath] = texture.place(cursorPosition)
        outputGraphics.drawImage(texture.image, cursorPosition.x, cursorPosition.y, null)
        cursorPosition = IntOffset(cursorPosition.x + texture.size.width, cursorPosition.y)
    }
    outputGraphics.dispose()

    outputGuiTextureAtlasFile.makeParentDirs()
    outputGuiTextureAtlasJsonFile.makeParentDirs()

    @OptIn(ExperimentalSerializationApi::class)
    Json.encodeToStream(placedTextures, outputGuiTextureAtlasJsonFile.outputStream())
    ImageIO.write(outputImage, "png", outputGuiTextureAtlasFile.outputStream())
}