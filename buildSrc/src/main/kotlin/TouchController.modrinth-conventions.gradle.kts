import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.modrinth.minotaur")
}

val libs = the<LibrariesForLibs>()

val modId: String by extra.properties
val modVersion: String by extra.properties
val modState: String by extra.properties
val gameVersion: String by extra.properties
val fabricApiVersion: String? by extra.properties
val modmenuVersion: String? by extra.properties

modrinth {
    val modType: String by extra.properties

    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set(modId)
    versionType.set(modState)
    when (modType) {
        "forge" -> {
            uploadFile.set(tasks.getByName("renameOutputJar"))
        }
        "fabric" -> {
            uploadFile.set(tasks.getByName("remapJar"))
        }
        else -> error("Bad modType: $modType")
    }
    gameVersions.add(gameVersion)
    versionNumber.set(version.toString())
    versionName.set(modVersion)

    dependencies {
        when (modType) {
            "forge" -> {
                // No dependencies
            }
            "fabric" -> {
                required.version("fabric-api", fabricApiVersion!!)
                required.version("fabric-language-kotlin", libs.versions.fabric.language.kotlin.get())
                optional.version("modmenu", modmenuVersion!!)
            }
            else -> error("Bad modType: $modType")
        }
    }
}
