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

include("mod:resources")
include("mod:common")
include("mod:fabric-1.21.1")
include("mod:fabric-1.21.3")
include("mod:fabric-1.21.4")
include("proxy-server")
include("proxy-client")
include("proxy-windows")
include("proxy-client-android")
include("proxy-server-android")
include("combine")
include("common-data")
