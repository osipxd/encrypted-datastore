plugins {
    com.redmadrobot.`kotlin-library`
    convention.publish
}

description = "Extensions to encrypt DataStore using Tink"

dependencies {
    api(kotlin("stdlib", version = libs.versions.kotlin.get()))
    api(libs.androidx.datastore.core)
    api(libs.tink)

    testImplementation(kotlin("test", version = libs.versions.kotlin.get()))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
