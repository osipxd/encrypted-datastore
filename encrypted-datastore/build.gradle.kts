import com.redmadrobot.build.dsl.ossrh

plugins {
    id("com.redmadrobot.kotlin-library")
    id("com.redmadrobot.publish")
}

description = "Extensions to encrypt DataStore using Tink"

dependencies {
    api(kotlin("stdlib", version = libs.versions.kotlin.get()))
    api(libs.androidx.datastore)
    api(libs.tink)
}

publishing {
    repositories {
        ossrh { credentials(PasswordCredentials::class) }
    }
}
