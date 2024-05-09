plugins {
    `java-platform`
    convention.publish
}

description = "Encryted DataStore BOM"

dependencies {
    constraints {
        api(projects.encryptedDatastore)
        api(projects.encryptedDatastorePreferences)
        api(projects.securityCryptoDatastore)
        api(projects.securityCryptoDatastorePreferences)
        api(libs.androidx.datastore)
        api(libs.androidx.datastore.core)
        api(libs.androidx.datastore.preferences)
        api(libs.androidx.datastore.preferences.core)
    }
}
