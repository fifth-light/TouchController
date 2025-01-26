plugins {
    `java-library`
}

group = "top.fifthlight.touchcontroller"
version = "0.0.1"

dependencies {
    api(libs.slf4j.api)
    implementation(libs.log4j.api)
    implementation(libs.log4j.core)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }

    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}