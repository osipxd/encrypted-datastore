plugins {
    convention.library.kotlin
}

description = "Extensions to encrypt DataStore Preferences using Tink"

dependencies {
    api(projects.encryptedDatastore)
    api(libs.androidx.datastore.preferences.core)

    testImplementation(kotlin("test", version = libs.versions.kotlin.get()))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(testFixtures(projects.encryptedDatastore))
}

// All Java classes here are package-private, so don't generate javadoc
tasks.getByName("javadoc") { enabled = false }
