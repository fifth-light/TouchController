import com.vanniktech.maven.publish.SonatypeHost

plugins {
    signing
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.maven.publish)
}

version = "0.0.2"

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
    coordinates("top.fifthlight.touchcontroller", "proxy-client-android", version.toString())
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    pom {
        name.set("Proxy client library of TouchController on Android")
        description.set("A library for connecting to the Minecraft mod TouchController on Android.")
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

android {
    namespace = "top.fifthlight.touchcontroller.proxy.client.android"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    api(project(":proxy-client"))
}