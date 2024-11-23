import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

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

// Make internal declarations from `datastore-preferences-core` accessible for this module
tasks.withType<KotlinJvmCompile>().configureEach {
    val datastoreLibrary = project.provider {
        libraries.first { it.name.startsWith("datastore-preferences-core") }.absoluteFile
    }
    friendPaths.from(datastoreLibrary)
}
