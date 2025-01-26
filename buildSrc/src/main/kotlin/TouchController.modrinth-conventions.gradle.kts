import com.modrinth.minotaur.TaskModrinthUpload
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.modrinth.minotaur")
}

val libs = the<LibrariesForLibs>()

val modId: String by extra.properties
val modName: String by extra.properties
val modVersion: String by extra.properties
val modState: String by extra.properties
val gameVersion: String by extra.properties
val fabricApiVersion: String? by extra.properties
val modmenuVersion: String? by extra.properties
val modType: String by extra.properties

tasks.withType<TaskModrinthUpload> {
    when (modType) {
        "forge" -> {
            dependsOn(tasks.getByName("renameOutputJar"))
        }

        "fabric" -> {
            // Nothing
        }

        else -> error("Bad modType: $modType")
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set(modId)
    versionType.set(modState)
    when (modType) {
        "forge" -> {
            uploadFile.set(layout.buildDirectory.file("libs/$modName-$version.jar"))
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
                optional.version("modmenu", modmenuVersion!!)
            }
            else -> error("Bad modType: $modType")
        }
    }
}
