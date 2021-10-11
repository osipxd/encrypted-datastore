pluginManagement {
    repositories {
        google() // Required if you use infrastructure-android
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "redmadrobot") {
                useModule("com.redmadrobot.build:infrastructure-android:${requested.version}")
            }
        }
    }
}

rootProject.name = "encrypted-datastore"
include("lib")
