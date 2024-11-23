plugins {
    id("com.android.library")
    kotlin("android")
    id("convention.publish")
}

applyKotlinDefaults()
android.applyAndroidDefaults()

dependencies {
    api(platform(project(":encrypted-datastore-bom")))
}
