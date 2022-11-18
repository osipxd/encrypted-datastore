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
    "encrypted-datastore-internal-visibility-hack",
    "security-crypto-datastore",
    "security-crypto-datastore-preferences",
)
