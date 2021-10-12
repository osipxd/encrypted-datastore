plugins {
    id("redmadrobot.kotlin-library")
    id("redmadrobot.publish")
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "io.github.osipxd"
version = "1.0.0-alpha01"

dependencies {
    api(kotlin("stdlib"))
    api("androidx.datastore:datastore-core:1.0.0")
    api("androidx.datastore:datastore-preferences-core:1.0.0")
    api("com.google.crypto.tink:tink-android:1.6.1")

    // It will be embedded into shadowJar and shouldn't be added to pom.xml file
    compileOnly(project(":encrypted-datastore-internal-visibility-hack"))
}

repositories {
    google()
}

// We use shadowJar instead
tasks.jar { enabled = false }

tasks.shadowJar {
    configurations = listOf(project.configurations.compileOnly.get())
    dependencies {
        include(project(":encrypted-datastore-internal-visibility-hack"))
    }
}

afterEvaluate {
    publishing {
        publications.getByName<MavenPublication>("maven") {
            // Exclude plain jar, leave shadowJar only
            setArtifacts(artifacts.filterNot { it.extension == "jar" && it.classifier == null })
        }
    }
}
