package top.fifthlight.touchcontroller.resource

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {
    val (language, legacyLanguage) = args
    val languageDir = File(language)
    val legacyLanguageDir = File(legacyLanguage)

    val languageFiles = languageDir.listFiles { it.extension.lowercase() == "json" } ?: arrayOf<File>()
    for (file in languageFiles) {
        val outputFile = File(legacyLanguageDir, "${file.nameWithoutExtension}.lang")
        val map: Map<String, String> = Json.decodeFromStream(file.inputStream())
        Properties().apply {
            putAll(map)
            store(outputFile.writer(), null)
        }
        val lines = outputFile.readLines().sorted().drop(1)
        outputFile.writer().use { writer ->
            lines.forEach { line ->
                writer.appendLine(line)
            }
        }
    }
}
