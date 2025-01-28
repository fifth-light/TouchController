package top.fifthlight.touchcontoller.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.util.regex.Pattern

abstract class CollectDependenciesTask : DefaultTask() {
    @get:Input
    val configurations: List<String> = listOf("shadow")

    @get:Input
    var groupExcludeRegex: Pattern? = null

    @get:OutputFile
    val dependencyCache: Provider<RegularFile> =
        project.layout.buildDirectory.file("generated/aboutLibraries/dependency_cache.json")

    @Internal
    protected lateinit var collectedDependencies: Map<String, Map<String, Set<String>>>

    private fun Set<ResolvedDependency>.getResolvedArtifacts(
        visitedDependencyNames: MutableSet<String>,
    ): Set<ResolvedArtifact> {
        val resolvedArtifacts = mutableSetOf<ResolvedArtifact>()
        for (resolvedDependency in this) {
            val name = resolvedDependency.name
            if (name !in visitedDependencyNames) {
                visitedDependencyNames += name

                try {
                    resolvedArtifacts += when {
                        resolvedDependency.moduleVersion == "unspecified" -> {
                            resolvedDependency.children.getResolvedArtifacts(
                                visitedDependencyNames = visitedDependencyNames
                            )
                        }

                        else -> resolvedDependency.allModuleArtifacts
                    }
                } catch (_: Throwable) {
                }
            }
        }

        return resolvedArtifacts
    }

    fun configure() {
        val configurationsDependencies: MutableMap<String, MutableMap<String, MutableSet<String>>> =
            sortedMapOf(compareBy<String> { it })
        val groupExcludeRegex = groupExcludeRegex?.toRegex()

        configurations
            .map { project.configurations.getByName(it) }
            .forEach { configuration ->
                val collectedDependencies =
                    configurationsDependencies.getOrPut(configuration.name) { sortedMapOf(compareBy<String> { it }) }
                val visitedDependencyNames = mutableSetOf<String>()
                try {
                    configuration
                        .resolvedConfiguration
                        .lenientConfiguration
                        .allModuleDependencies
                        .getResolvedArtifacts(visitedDependencyNames)
                        .filter { artifact ->
                            if (groupExcludeRegex == null) {
                                true
                            } else {
                                !artifact.moduleVersion.id.group.matches(groupExcludeRegex)
                            }
                        }
                        .forEach { resArtifact ->
                            val identifier = "${resArtifact.moduleVersion.id.group.trim()}:${resArtifact.name.trim()}"
                            val versions = collectedDependencies.getOrPut(identifier) { LinkedHashSet() }
                            versions.add(resArtifact.moduleVersion.id.version.trim())
                        }
                } catch (t: Throwable) {
                }
            }

        collectedDependencies = configurationsDependencies
    }

    @TaskAction
    fun action() {
        if (!::collectedDependencies.isInitialized) {
            configure()
        }

        dependencyCache.get().asFile.parentFile.mkdirs()
        val outputMap = mapOf(
            "dependencies" to collectedDependencies
        )
        dependencyCache.get().asFile.writeText(groovy.json.JsonOutput.toJson(outputMap))
    }
}