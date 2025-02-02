package top.fifthlight.touchcontroller.about

import kotlinx.serialization.json.Json
import top.fifthlight.touchcontroller.BuildInfo
import java.io.InputStream

object ResourcesAboutInfoProvider : AboutInfoProvider {
    private fun getResourceAsStream(name: String): InputStream? = this.javaClass.classLoader.getResourceAsStream(name)
    private fun readResource(name: String): String? = getResourceAsStream(name)?.reader()?.use { it.readText() }

    private val jsonFormat = Json {
        ignoreUnknownKeys = true
    }

    override val aboutInfo: AboutInfo by lazy {
        val modLicense = readResource("LICENSE_${BuildInfo.MOD_NAME}")
        val librariesJson = readResource("aboutlibraries.json")
        val libraries = librariesJson?.let { librariesJson ->
            jsonFormat.decodeFromString<Libs>(librariesJson)
        }
        AboutInfo(
            modLicense = modLicense,
            libraries = libraries,
        )
    }
}