import com.redmadrobot.build.dsl.ossrh

plugins {
    id("com.redmadrobot.kotlin-library")
    id("com.redmadrobot.publish")
}

description = "Extensions to encrypt DataStore using Tink"

val hackProject = project(":encrypted-datastore-internal-visibility-hack")
dependencies {
    api(kotlin("stdlib", version = libs.versions.kotlin.get()))
    api(libs.androidx.datastore)
    api(libs.androidx.datastore.preferences)
    api(libs.tink)

    // It will be embedded into jar and shouldn't be added to pom.xml file
    compileOnly(hackProject)
}

// Embed hack into the jar
tasks.jar {
    from(hackProject.sourceSets["main"].output)
}

publishing {
    repositories {
        ossrh { credentials(PasswordCredentials::class) }
    }
}
