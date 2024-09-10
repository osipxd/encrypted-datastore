plugins {
    id("com.android.library")
    kotlin("android")
    id("convention.library.common")
    id("convention.publish")
}

android {
    compileSdk = 35

    // Min SDK should be aligned with min SDK in androidx.security:security-crypto
    defaultConfig.minSdk = 23

    buildFeatures {
        resValues = false
        androidResources = false
        shaders = false
    }

    lint {
        checkDependencies = true
        abortOnError = true
        warningsAsErrors = true
    }
}
