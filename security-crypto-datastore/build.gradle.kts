plugins {
    convention.library.android
}

description = "AndroidX Security Kotlin Extensions (with DataStore support)"

android {
    namespace = "$group.security.datastore"
}

dependencies {
    implementation(project(":encrypted-datastore"))
    api(libs.androidx.datastore)
    api(libs.androidx.security.crypto)
}
