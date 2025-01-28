package top.fifthlight.touchcontroller.about

import com.mikepenz.aboutlibraries.Libs
import top.fifthlight.touchcontroller.BuildInfo
import java.io.InputStream

object ResourcesAboutInfoProvider : AboutInfoProvider {
    private fun getResourceAsStream(name: String): InputStream? = this.javaClass.classLoader.getResourceAsStream(name)
    private fun readResource(name: String): String? = getResourceAsStream(name)?.reader()?.use { it.readText() }

    override val aboutInfo: AboutInfo by lazy {
        val modLicense = readResource("LICENSE_${BuildInfo.MOD_NAME}")
        val librariesJson = readResource("aboutlibraries.json")
        val libraries = librariesJson?.let { librariesJson ->
            Libs.Builder()
                .withJson(librariesJson)
                .build()
        }
        AboutInfo(
            modLicense = modLicense,
            libraries = libraries,
        )
    }
}