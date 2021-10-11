plugins {
    id("redmadrobot.android-library")
}

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib"))
    api("androidx.datastore:datastore:1.0.0")
    api("com.google.crypto.tink:tink-android:1.6.1")
}
