import com.redmadrobot.build.dsl.kotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import internal.java

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

apiValidation {
    ignoredPackages.add("io.github.osipxd.datastore.encrypted.internal")
}
