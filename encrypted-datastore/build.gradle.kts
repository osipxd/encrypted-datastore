plugins {
    convention.library.kmp
    convention.publish
}

description = "Extensions to encrypt DataStore using Tink"

android {
    namespace = "$group.datastore.encrypted"
}

kotlin.sourceSets {
    commonJvmMain.dependencies {
        api(kotlin("stdlib", version = libs.versions.kotlin.get()))
        api(libs.androidx.datastore)
        compileOnly(libs.tink)
    }
    jvmMain.dependencies {
        api(libs.tink)
    }
    androidMain.dependencies {
        api(libs.tink.android)
    }
}
