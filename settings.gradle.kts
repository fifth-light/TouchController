pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		mavenCentral()
        google()
		gradlePluginPortal()
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "TouchController"

include("mod")
include("proxy-server")
include("proxy-client")
include("proxy-windows")
include("proxy-client-android")
include("proxy-server-android")
include("combine")
include("common-data")
