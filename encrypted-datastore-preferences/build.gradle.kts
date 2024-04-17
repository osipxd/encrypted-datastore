plugins {
    convention.library.kotlin
}

description = "Extensions to encrypt DataStore Preferences using Tink"

dependencies {
    api(project(":encrypted-datastore"))
    api(libs.androidx.datastore.preferences.core)
}

// All Java classes here are package-private, so don't generate javadoc
tasks.getByName("javadoc") { enabled = false }
