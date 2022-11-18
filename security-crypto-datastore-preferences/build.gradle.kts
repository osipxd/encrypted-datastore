plugins {
    com.redmadrobot.`android-library`
    convention.publish
}

description = "AndroidX Security Kotlin Extensions (with DataStore support)"

android {
    namespace = "$group.security.datastore.preferences"
}

dependencies {
    implementation(project(":encrypted-datastore-preferences"))
    api(project(":security-crypto-datastore"))
    api(libs.androidx.datastore.preferences)
}
