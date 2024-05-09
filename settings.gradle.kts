@file:Suppress("UnstableApiUsage")

rootProject.name = "encrypted-datastore-root"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
        google()
    }
}

include(
    "encrypted-datastore-bom",
    "encrypted-datastore",
    "encrypted-datastore-preferences",
    "security-crypto-datastore",
    "security-crypto-datastore-preferences",
)

include(":sample")
