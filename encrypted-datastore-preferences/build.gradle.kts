plugins {
    convention.library
}

description = "Extensions to encrypt DataStore Preferences using Tink"

val hackProject = project(":encrypted-datastore-internal-visibility-hack")
dependencies {
    api(project(":encrypted-datastore"))
    api(libs.androidx.datastore.preferences)

    // It will be embedded into jar and shouldn't be added to pom.xml file
    compileOnly(hackProject)
}

// Embed hack into the jar
tasks.jar {
    from(hackProject.sourceSets["main"].output)
}
