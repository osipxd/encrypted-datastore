plugins {
    convention.library.kotlin
}

description = "Extensions to encrypt DataStore Preferences using Tink"

dependencies {
    api(project(":encrypted-datastore"))
    api(libs.androidx.datastore.preferences.core)

    testImplementation(kotlin("test", version = libs.versions.kotlin.get()))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(testFixtures(project(":encrypted-datastore")))
}

// All Java classes here are package-private, so don't generate javadoc
tasks.getByName("javadoc") { enabled = false }
