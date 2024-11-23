import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    convention.library.kmp
    convention.publish
}

description = "Extensions to encrypt DataStore Preferences using Tink"

android {
    namespace = "$group.datastore.encrypted.preferences"
}

kotlin.sourceSets {
    commonJvmMain.dependencies {
        api(projects.encryptedDatastore)
        api(libs.androidx.datastore.preferences)
    }
    commonJvmTest.dependencies {
        implementation(projects.encryptedDatastore.testFixtures)
    }
}

// Make internal declarations from `datastore-preferences-core` accessible for this module
tasks.withType<KotlinJvmCompile>().configureEach {
    val datastoreLibrary = project.provider {
        libraries.first { it.name.startsWith("datastore-preferences-core") }.absoluteFile
    }
    friendPaths.from(datastoreLibrary)
}
