import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

plugins {
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
}

with(kotlinExtension) {
    jvmToolchain(11)

    explicitApi()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    "api"(platform(project(":encrypted-datastore-bom")))
}

apiValidation {
    ignoredPackages.add("io.github.osipxd.datastore.encrypted.internal")
    nonPublicMarkers.add("androidx.annotation.RestrictTo")
}
