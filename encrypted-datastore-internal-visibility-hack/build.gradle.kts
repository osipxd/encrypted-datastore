plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("androidx.datastore:datastore-preferences-core:1.0.0")
}

repositories {
    mavenCentral()
    google()
}
