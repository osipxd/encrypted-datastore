plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.android.gradlePlugin)

    implementation(libs.infrastructure.publish)
    implementation(libs.mavenPublishPlugin)

    implementation(libs.kotlinx.binaryCompatibilityValidator)
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}
