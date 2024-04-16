rootProject.name = "encrypted-datastore"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        mavenCentral()
        google()
    }
}

include(
    "encrypted-datastore",
    "encrypted-datastore-preferences",
    "security-crypto-datastore",
    "security-crypto-datastore-preferences",
)
