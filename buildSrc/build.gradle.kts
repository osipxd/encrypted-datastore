plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.infrastructure.kotlin)
    implementation(libs.infrastructure.publish)
}

repositories {
    mavenCentral()
}
