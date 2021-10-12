plugins {
    id("redmadrobot.kotlin-library")
}

dependencies {
    api(kotlin("stdlib"))
    api("androidx.datastore:datastore-core:1.0.0")
    api("com.google.crypto.tink:tink-android:1.6.1")
}

repositories {
    google()
}
