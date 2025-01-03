plugins {
    java
}

val javaVersion: String by extra.properties
val javaVersionNum = javaVersion.toInt()

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersionNum)
    }

    sourceCompatibility = JavaVersion.toVersion(javaVersionNum)
    targetCompatibility = JavaVersion.toVersion(javaVersionNum)
}
