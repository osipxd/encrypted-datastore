import com.redmadrobot.build.dsl.kotlinCompile
import internal.java
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
}

kotlinCompile {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    "api"(platform(project(":encrypted-datastore-bom")))
}

apiValidation {
    ignoredPackages.add("io.github.osipxd.datastore.encrypted.internal")
    nonPublicMarkers.add("androidx.annotation.RestrictTo")
}
