plugins {
    convention.library
}

description = "Extensions to encrypt DataStore using Tink"

dependencies {
    api(kotlin("stdlib", version = libs.versions.kotlin.get()))
    api(libs.androidx.datastore)
    api(libs.tink)
}
