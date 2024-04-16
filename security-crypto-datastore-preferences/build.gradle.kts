plugins {
    convention.library.android
}

description = "AndroidX Security Kotlin Extensions (with DataStore support)"

android {
    namespace = "$group.security.datastore.preferences"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }
}

dependencies {
    implementation(project(":encrypted-datastore-preferences"))
    api(project(":security-crypto-datastore"))
    api(libs.androidx.datastore.preferences)

    androidTestImplementation(kotlin("test", version = libs.versions.kotlin.get()))
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.core)
}
