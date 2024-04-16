plugins {
    convention.library.kotlin
}

description = "Extensions to encrypt DataStore Preferences using Tink"

dependencies {
    api(project(":encrypted-datastore"))
    api(libs.androidx.datastore.preferences.core)
}
