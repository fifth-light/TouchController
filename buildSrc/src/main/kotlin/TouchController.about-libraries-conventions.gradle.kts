import com.mikepenz.aboutlibraries.plugin.AboutLibrariesCollectorTask
import com.mikepenz.aboutlibraries.plugin.AboutLibrariesTask
import top.fifthlight.touchcontoller.gradle.CollectDependenciesTask
import java.util.regex.Pattern

plugins {
    java
    id("com.mikepenz.aboutlibraries.plugin")
}

aboutLibraries {
    registerAndroidTasks = false
}

project.afterEvaluate {
    val collectDependencies = tasks.getByName<AboutLibrariesCollectorTask>("collectDependencies")
    val exportLibraryDefinitions = tasks.getByName<AboutLibrariesTask>("exportLibraryDefinitions")

    collectDependencies.enabled = false

    val collectShadowDependencies = tasks.register<CollectDependenciesTask>("collectShadowDependencies") {
        groupExcludeRegex = Pattern.compile("top\\.fifthlight\\.touchcontroller")
        configure()
    }

    exportLibraryDefinitions.apply {
        dependsOn(collectShadowDependencies)
    }

    tasks.register<Copy>("copyLibraryDefinitions") {
        description =
            "Copy the relevant meta data for the AboutLibraries plugin to display dependencies to sources directory"
        group = "Build"
        dependsOn(exportLibraryDefinitions)
        from(exportLibraryDefinitions.resultDirectory.file("aboutlibraries.json"))
        destinationDir = project.file("src/main/resources")
    }
}
