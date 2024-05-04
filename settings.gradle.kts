rootProject.name = "encrypted-datastore"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

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
