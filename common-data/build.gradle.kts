import com.vanniktech.maven.publish.SonatypeHost

plugins {
    signing
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.maven.publish)
}

version = "0.0.1"

tasks.withType<Sign>().configureEach {
    onlyIf("have publish task") {
        gradle.taskGraph.hasTask("publish")
    }
}

signing {
    useGpgCmd()
    sign(configurations.archives.get())
}

mavenPublishing {
    coordinates("top.fifthlight.touchcontroller", "common-data", version.toString())
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    pom {
        name.set("Common data library of TouchController")
        description.set("A library for providing common data classes for the Minecraft mod TouchController.")
        inceptionYear.set("2024")
        url.set("https://github.com/fifth-light/TouchController")
        licenses {
            license {
                name.set("GNU Lesser General Public License v3.0")
                url.set("https://www.gnu.org/licenses/lgpl-3.0.html")
                distribution.set("https://www.gnu.org/licenses/lgpl-3.0.html")
            }
        }
        developers {
            developer {
                id.set("fifth_light")
                name.set("fifth_light")
                url.set("https://github.com/fifth-light/")
            }
        }
        scm {
            url.set("https://github.com/fifth-light/TouchController")
            connection.set("scm:git:https://github.com/fifth-light/TouchController.git")
            developerConnection.set("scm:git:ssh://git@github.com:fifth-light/TouchController.git")
        }
    }
}

base {
    archivesName = "TouchController-CommonData"
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
}

kotlin {
    jvmToolchain(8)
}
