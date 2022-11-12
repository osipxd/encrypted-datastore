import com.redmadrobot.build.dsl.ossrh

plugins {
    id("com.redmadrobot.kotlin-library") version "0.17"
    id("com.redmadrobot.publish")
}

description = "Extensions to encrypt DataStore using Tink"
group = "io.github.osipxd"
version = "1.0.0-alpha02"

val hackProject = project(":encrypted-datastore-internal-visibility-hack")
dependencies {
    api(kotlin("stdlib", version = "1.7.21"))
    api("androidx.datastore:datastore-core:1.0.0")
    api("androidx.datastore:datastore-preferences-core:1.0.0")
    api("com.google.crypto.tink:tink-android:1.7.0")

    // It will be embedded into jar and shouldn't be added to pom.xml file
    compileOnly(hackProject)
}

repositories {
    google()
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
