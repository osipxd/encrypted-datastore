plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.android.gradlePlugin)
    implementation(libs.android.cacheFix)

    implementation(libs.infrastructure.android)
    implementation(libs.infrastructure.kotlin)
    implementation(libs.infrastructure.publish)

    implementation(libs.kotlinx.binaryCompatibilityValidator)
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}
