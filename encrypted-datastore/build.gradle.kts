plugins {
    convention.library.kotlin
}

description = "Extensions to encrypt DataStore using Tink"

dependencies {
    api(kotlin("stdlib", version = libs.versions.kotlin.get()))
    api(libs.androidx.datastore.core)
    api(libs.tink)

    testImplementation(kotlin("test", version = libs.versions.kotlin.get()))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}
