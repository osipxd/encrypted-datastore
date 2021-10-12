plugins {
    id("redmadrobot.kotlin-library")
    id("redmadrobot.publish")
}

description = "Extensions to encrypt DataStore using Tink"
group = "io.github.osipxd"
version = "1.0.0-alpha02"

val hackProject = project(":encrypted-datastore-internal-visibility-hack")
dependencies {
    api(kotlin("stdlib"))
    api("androidx.datastore:datastore-core:1.0.0")
    api("androidx.datastore:datastore-preferences-core:1.0.0")
    api("com.google.crypto.tink:tink-android:1.6.1")

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
        maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
            name = "ossrh"
            credentials(PasswordCredentials::class)
        }
    }
}
