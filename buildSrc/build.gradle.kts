plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.android.gradlePlugin)

    implementation(libs.infrastructure.publish)
    implementation(libs.mavenPublishPlugin)

    implementation(libs.kotlinx.binaryCompatibilityValidator)

    // A hack to make version catalogs accessible from buildSrc sources
    // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}
